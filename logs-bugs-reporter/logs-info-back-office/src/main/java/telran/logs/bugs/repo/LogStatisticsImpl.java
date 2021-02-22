package telran.logs.bugs.repo;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
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
		List<AggregationOperation> aggregationOperations = new ArrayList<AggregationOperation>();
		return mongoTemplate.aggregate(getPipeline(groupField, projectField, aggregationOperations),
				LogTypeCount.class);
	}

	@Override
	public Flux<LogTypeCount> getMostEncounteredExceptionTypes(int nExceptions) {
		groupField = LogDoc.LOG_TYPE;
		projectField = LogTypeCount.LOG_TYPE;
		List<AggregationOperation> aggregationOperations = new ArrayList<AggregationOperation>();
		return mongoTemplate.aggregate(getLimitPipeline(groupField, projectField, aggregationOperations, nExceptions),
				LogTypeCount.class);
	}

	@Override
	public Flux<ArtifactCount> getArtifactCount() {
		groupField = LogDoc.ARTIFACT;
		projectField = ArtifactCount.ARTIFACT;
		List<AggregationOperation> aggregationOperations = new ArrayList<AggregationOperation>();
		return mongoTemplate.aggregate(getPipeline(groupField, projectField, aggregationOperations),
				ArtifactCount.class);
	}

	@Override
	public Flux<ArtifactCount> getMostEncounteredArtifacts(int nArtifacts) {
		groupField = LogDoc.ARTIFACT;
		projectField = ArtifactCount.ARTIFACT;
		List<AggregationOperation> aggregationOperations = new ArrayList<AggregationOperation>();
		return mongoTemplate.aggregate(getLimitPipeline(groupField, projectField, aggregationOperations, nArtifacts),
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
		fillOperationsLimitList(groupField, ProjectField, aggregationOperations, limit);
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

	private void fillOperationsLimitList(String groupField, String projectField,
			List<AggregationOperation> aggregationOperations, int limit) {
		GroupOperation groupOperation = Aggregation.group(groupField).count().as(COUNT);
		ProjectionOperation projectionOperation = Aggregation.project(COUNT).and("_id").as(projectField);
		SortOperation sortOperation = Aggregation.sort(Direction.DESC, COUNT);
		LimitOperation limitOperation = Aggregation.limit(limit);
		aggregationOperations.add(groupOperation);
		aggregationOperations.add(sortOperation);
		aggregationOperations.add(limitOperation);
		aggregationOperations.add(projectionOperation);
	}

}