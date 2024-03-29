package com.bigdata.backend.repositories;

import com.bigdata.backend.dto.*;
import org.neo4j.driver.Query;
import org.neo4j.driver.Record;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class QueryRepositoryImpl implements QueryRepository {

    private final Neo4JDriver neo4JDriver;

    public QueryRepositoryImpl(Neo4JDriver neo4JDriver) {
        this.neo4JDriver = neo4JDriver;
    }

    @Override
    public List<Map<String, Object>> getNodes(NodesRequest nodesRequest) {
        List<Map<String, Object>> items = new ArrayList<>();
        try (var session = this.neo4JDriver.getDriver().session()) {
            String queryString = CypherQueryBuilder.generateNodesQuery(nodesRequest);
            var query = new Query(queryString);
            var result = session.run(query);
            while (result.hasNext()) {
                Record record = result.next();
                items.add(record.get("n").asMap());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return items;
    }

    @Override
    public NodesCountResponse getNodesCount(NodesCountRequest nodesCountRequest) {
        NodesCountResponse nodesCountResponse = new NodesCountResponse(0);
        try (var session = this.neo4JDriver.getDriver().session()) {
            String queryString = CypherQueryBuilder.generateNodesCountQuery(nodesCountRequest);
            var query = new Query(queryString);
            var result = session.run(query);
            while (result.hasNext()) {
                Record record = result.next();
                nodesCountResponse.setCount(record.get("count").asInt());
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return nodesCountResponse;
    }

    @Override
    public List<RelatedNodesResponse> getRelatedNodes(RelatedNodesRequest relatedNodesRequest) {
        List<RelatedNodesResponse> items = new ArrayList<>();
        try (var session = this.neo4JDriver.getDriver().session()) {
            String queryString = CypherQueryBuilder.generateRelatedNodesQuery(relatedNodesRequest);
            System.out.println(queryString);
            var query = new Query(queryString);
            var result = session.run(query);

            while (result.hasNext()) {
                Record record = result.next();
                RelatedNodesResponse relatedNodesResponse = new RelatedNodesResponse();
                NodeResponse nodeResponse = new NodeResponse();
                RelationshipResponse relationshipResponse = new RelationshipResponse();
                String nodeLabel;
                String relationshipLabel;

                nodeLabel = record.get("relatedNode").asNode().labels().iterator().next();
                nodeResponse.setName(nodeLabel);
                nodeResponse.setProperties(record.get("relatedNode").asMap());

                relationshipLabel = record.get("r").asRelationship().type();
                relationshipResponse.setName(relationshipLabel);
                relationshipResponse.setProperties(record.get("r").asMap());

                relatedNodesResponse.setNode(nodeResponse);
                relatedNodesResponse.setRelationship(relationshipResponse);

                items.add(relatedNodesResponse);
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return items;
    }

    @Override
    public List<Map<String, Object>> getIdentifiers() {
        List<Map<String, Object>> identifiers = new ArrayList<>();
        try (var session = this.neo4JDriver.getDriver().session()) {
            String queryString = "Match (n:Identifier) RETURN n";
            var query = new Query(queryString);
            var result = session.run(query);
            while (result.hasNext()) {
                Record record = result.next();
                identifiers.add(record.get("n").asMap());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return identifiers;
    }

    @Override
    public List<String> getNodeProperties(String label) {
        List<String> properties = new ArrayList<>();
        try (var session = this.neo4JDriver.getDriver().session()) {
            String queryString = "MATCH (n:" + label + ")\n" +
                    "WITH n LIMIT 1000\n" +
                    "UNWIND keys(n) as key\n" +
                    "RETURN distinct key";
            var query = new Query(queryString);
            var result = session.run(query);
            while (result.hasNext()) {
                Record record = result.next();
                properties.add(record.get("key").asString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return properties;
    }
}
