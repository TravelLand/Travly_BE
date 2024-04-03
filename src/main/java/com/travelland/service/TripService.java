package com.travelland.service;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelland.domain.Member;
import com.travelland.domain.Trip;
import com.travelland.domain.TripHashtag;
import com.travelland.dto.TripDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.MemberRepository;
import com.travelland.repository.TripHashtagRepository;
import com.travelland.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static com.travelland.domain.QTrip.trip;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final MemberRepository memberRepository;
    private final TripHashtagRepository tripHashtagRepository;
    private final TripImageService tripImageService;
    private final TripLikeService tripLikeService;
    private final TripScrapService tripScrapService;
    private final JPAQueryFactory jpaQueryFactory;

    @Transactional
    public TripDto.CreateResponse createTrip(TripDto.CreateRequest requestDto, List<MultipartFile> imageList, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Trip trip = tripRepository.save(new Trip(requestDto, member));//여행정보 저장

        if (!requestDto.getHashTag().isEmpty()) //해쉬태그 저장
            requestDto.getHashTag().forEach(hashtagTitle -> tripHashtagRepository.save(new TripHashtag(hashtagTitle, trip)));

        if (!imageList.isEmpty()) //여행정보 이미지 정보 저장
            tripImageService.createTripImage(imageList, trip);

        return new TripDto.CreateResponse(trip.getId());
    }

    @Transactional(readOnly = true)
    public List<TripDto.GetListResponse> getTripList(int page, int size, String sort, boolean ASC) {
        Order order = (ASC) ? Order.ASC : Order.DESC;
        OrderSpecifier orderSpecifier = createOrderSpecifier(sort, order);
        List<Trip> tripList = jpaQueryFactory.selectFrom(trip)
                .orderBy(orderSpecifier, trip.id.desc())
                .limit(size)
                .offset((long) (page - 1) * size)
                .fetch();

        // 게시글 목록 조회된 여행정보의 썸네일 URL 가져오기
        return tripList.stream()
                .map(trip -> new TripDto.GetListResponse(trip, tripImageService.getTripImageThumbnailUrl(trip)))
                .collect(Collectors.toList());
    }

    @Transactional
    public TripDto.GetResponse getTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        trip.increaseViewCount(); //조회수 증가

        // 해쉬태그 가져오기
        List<String> hashTag = tripHashtagRepository.findAllByTrip(trip).stream()
                .map(TripHashtag::getTitle).toList();

        List<String> imageUrlList = tripImageService.getTripImageUrl(trip);

        return new TripDto.GetResponse(trip, hashTag, imageUrlList);
    }

    @Transactional
    public TripDto.UpdateResponse updateTrip(Long tripId, TripDto.UpdateRequest requestDto, List<MultipartFile> imageList, String email) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!trip.getMember().getEmail().equals(email))
            throw new CustomException(ErrorCode.POST_UPDATE_NOT_PERMISSION);

        //해쉬태그 수정
        tripHashtagRepository.deleteByTrip(trip);

        if (!requestDto.getHashTag().isEmpty())
            requestDto.getHashTag().forEach(hashtagTitle -> tripHashtagRepository.save(new TripHashtag(hashtagTitle, trip)));

        //이미지 수정
        tripImageService.deleteTripImage(trip);

        if (!imageList.isEmpty())
            tripImageService.createTripImage(imageList, trip);

        //여행정보 수정
        trip.update(requestDto);

        return new TripDto.UpdateResponse(trip.getId());
    }

    @Transactional
    public void deleteTrip(Long tripId, String email) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!trip.getMember().getEmail().equals(email))
            throw new CustomException(ErrorCode.POST_DELETE_NOT_PERMISSION);

        // 여행정보 엔티티와 관련된 데이터 삭제
        tripImageService.deleteTripImage(trip);
        tripLikeService.deleteTripLike(trip);
        tripScrapService.deleteTripScrap(trip);

        tripHashtagRepository.deleteByTrip(trip);
        tripRepository.delete(trip);
    }

    @Transactional(readOnly = true)
    public List<TripDto.GetMyTripListResponse> getMyTripList(int page, int size, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<Trip> tripList = jpaQueryFactory.selectFrom(trip)
                .where(trip.member.eq(member))
                .orderBy(trip.createdAt.desc())
                .limit(size)
                .offset((long) (page - 1) * size)
                .fetch();

        return tripList.stream()
                .map(trip -> new TripDto.GetMyTripListResponse(trip, tripImageService.getTripImageThumbnailUrl(trip)))
                .collect(Collectors.toList());

    }

    // 목록 정렬 방식, 기준 설정 메서드
    private OrderSpecifier createOrderSpecifier(String sort, Order order) {
        return switch (sort) {
            case "viewCount" -> new OrderSpecifier<>(order, trip.viewCount);
            case "title" -> new OrderSpecifier<>(order, trip.title);
            default -> new OrderSpecifier<>(order, trip.createdAt);
        };
    }
}
