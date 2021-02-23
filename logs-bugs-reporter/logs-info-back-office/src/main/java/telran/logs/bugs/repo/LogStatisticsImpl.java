package telran.logs.bugs.repo;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.*;
import telran.logs.bugs.mongo.doc.LogDoc;

public class LogStatisticsImpl implements LogStatistics {
	private static final String COUNT = "count";
	String groupField;
	String projectField;
	@Autowired
	ReactiveMongoTemplate mongoTemplate;

	@Override
	public Flux<LogTypeCount> getLogTypeCounts() {
		groupField = LogDoc.LOG_TYPE;
		projectField = LogTypeCount.LOG_TYPE;
		return mongoTemplate.aggregate(getPipeline(groupField, projectField, new ArrayList<>()),
				LogTypeCount.class);
	}

	@Override
	public Flux<LogTypeCount> getMostEncounteredExceptionTypes(int nExceptions) {
		groupField = LogDoc.LOG_TYPE;
		projectField = LogTypeCount.LOG_TYPE;
		List<AggregationOperation> aggregationOperations = new ArrayList<>();
		aggregationOperations.add(Aggregation.match(Criteria.where(LogDoc.LOG_TYPE).ne(LogType.NO_EXCEPTION)));
		return mongoTemplate.aggregate(getLimitPipeline(groupField, projectField, aggregationOperations, nExceptions),
				LogTypeCount.class);
	}

	@Override
	public Flux<ArtifactCount> getArtifactCount() {
		groupField = LogDoc.ARTIFACT;
		projectField = ArtifactCount.ARTIFACT;
		return mongoTemplate.aggregate(getPipeline(groupField, projectField, new ArrayList<>()),
				ArtifactCount.class);
	}

	@Override
	public Flux<ArtifactCount> getMostEncounteredArtifacts(int nArtifacts) {
		groupField = LogDoc.ARTIFACT;
		projectField = ArtifactCount.ARTIFACT;
		return mongoTemplate.aggregate(getLimitPipeline(groupField, projectField, new ArrayList<>(), nArtifacts),
				ArtifactCount.class);
	}

	private TypedAggregation<LogDoc> getPipeline(String groupField, String projectField,
			List<AggregationOperation> aggregationOperations) {
		fillOperationsList(groupField, projectField, aggregationOperations);
		TypedAggregation<LogDoc> pipeline = Aggregation.newAggregation(LogDoc.class, aggregationOperations);
		return pipeline;
	}

	private TypedAggregation<LogDoc> getLimitPipeline(String groupField, String ProjectField,
			List<AggregationOperation> aggregationOperations, int limit) {
		fillOperationsList(groupField, ProjectField, aggregationOperations);
		aggregationOperations.add(Aggregation.limit(limit));
		TypedAggregation<LogDoc> pipeline = Aggregation.newAggregation(LogDoc.class, aggregationOperations);
		return pipeline;
	}

	private void fillOperationsList(String groupField, String projectField,
			List<AggregationOperation> aggregationOperations) {
		GroupOperation groupOperation = Aggregation.group(groupField).count().as(COUNT);
		ProjectionOperation projectionOperation = Aggregation.project(COUNT).and("_id").as(projectField);
		SortOperation sortOperation = Aggregation.sort(Direction.DESC, COUNT);
		aggregationOperations.add(groupOperation);
		aggregationOperations.add(sortOperation);
		aggregationOperations.add(projectionOperation);
	}

}