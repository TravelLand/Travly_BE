package com.travelland.repository.trip.es;

import com.travelland.dto.trip.TripDto;
import com.travelland.esdoc.TripSearchDoc;
import com.travelland.global.job.DataIntSet;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

@RequiredArgsConstructor
@Component
public class CustomTripRepositoryImpl implements CustomTripRepository {
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public SearchHits<TripSearchDoc> searchByTitle(String title, Pageable pageable) {
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withPageable(pageable);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        Arrays.stream(title.split("\\s+"))
                .forEach(word -> boolQueryBuilder.must(QueryBuilders.matchQuery("title", word)));
        boolQueryBuilder.must(QueryBuilders.matchQuery("isPublic", true));
        searchQueryBuilder.withQuery(boolQueryBuilder);

        return elasticsearchOperations.search(searchQueryBuilder.build(), TripSearchDoc.class);
    }

    @Override
    public SearchHits<TripSearchDoc> searchByHashtag(String hashtag, Pageable pageable) {

        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withPageable(pageable);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("is_public", true));
        boolQueryBuilder.must(QueryBuilders.matchQuery("hashtag", hashtag));
        searchQueryBuilder.withQuery(boolQueryBuilder);

        return elasticsearchOperations.search(searchQueryBuilder.build(), TripSearchDoc.class);
    }

    @Override
    public List<String> searchByAddress(String address) {

        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withPageable(PageRequest.of(0, 7));
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        Arrays.stream(address.split("\\s+"))
                .map(part -> QueryBuilders.matchQuery("address", part).operator(Operator.AND)
                ).forEach(boolQueryBuilder::must);
        searchQueryBuilder.withQuery(boolQueryBuilder);

        return elasticsearchOperations.search(searchQueryBuilder.build(),
                        TripSearchDoc.class)
                .stream()
                .map(SearchHit::getContent)
                .map(TripSearchDoc::getAddress)
                .toList();
    }

    @Override
    public SearchHits<TripSearchDoc> searchByEmail(Pageable pageable, String email) {
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withPageable(pageable);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("email", email));
        searchQueryBuilder.withQuery(boolQueryBuilder);

        return elasticsearchOperations.search(searchQueryBuilder.build(), TripSearchDoc.class);
    }

    @Override
    public SearchHits<TripSearchDoc> findAllList(Pageable pageable) {
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withPageable(pageable);

        return elasticsearchOperations.search(searchQueryBuilder.build(), TripSearchDoc.class);
    }

    @Override
    public List<DataIntSet> readViewCount(Pageable pageable) {
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withPageable(pageable);
        searchQueryBuilder.withFields("trip_id", "view_count");

        return elasticsearchOperations.search(searchQueryBuilder.build(), TripSearchDoc.class)
                .stream()
                .map(SearchHit::getContent)
                .map(data -> new DataIntSet(data.getTripId(), data.getViewCount()))
                .toList();
    }

    @Override
    public List<TripDto.GetList> findRankList(List<Long> keys) {
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withQuery(termsQuery("trip_id", keys));

        return elasticsearchOperations.search(searchQueryBuilder.build(), TripSearchDoc.class)
                .stream()
                .map(hit -> new TripDto.GetList(hit.getContent()))
                .toList();
    }
}