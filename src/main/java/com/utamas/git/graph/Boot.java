package com.utamas.git.graph;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.utamas.git.graph.d3.GraphToD3GraphConverter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.utamas.git.graph.GraphBuilder.GraphNode;

public class Boot {
    public static void main(String... args) throws IOException, GitAPIException {
        String pathname = "/Users/tamasutasi/projects/server-setup/.git";

        GraphBuilder graphBuilder = new GraphBuilder();

        Repository repository = new FileRepositoryBuilder().setGitDir(new File(pathname)).build();

        Git git = new Git(repository);

        Map<AnyObjectId, Set<Ref>> commitChainHeadIdsToRef = repository.getAllRefsByPeeledObjectId();
        commitChainHeadIdsToRef.keySet().stream().forEach(commitChainHead -> {

            Set<String> labels = getStickyNotesLabels(commitChainHeadIdsToRef.get(commitChainHead));
            try {
                Iterable<RevCommit> commitChain = git.log().add(commitChainHead).call();

                commitChain.forEach(commit -> {
                    String commitHash = getCommitHash(commit);

                    graphBuilder.withNodeWithLabels(commitHash, labels);

                    getParentCommitHashes(commit)
                            .stream()
                            .forEach(parentCommit ->
                                    graphBuilder.withEdge(GraphNode.of(commitHash, getAuthorEmail(commit)), parentCommit)
                            );
                });

            } catch (GitAPIException | MissingObjectException | IncorrectObjectTypeException cause) {
                throw new IllegalStateException(cause);
            }
        });


        Iterable<RevCommit> allCommits = git.log().all().call();

        allCommits.forEach(commit -> {
            String commitHash = ObjectId.toString(commit.getId());
            String authorEmail = getAuthorEmail(commit);

            graphBuilder.assignNodeToGroup(commitHash, authorEmail);
        });

        GraphBuilder.Graph graph = graphBuilder.build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        GraphToD3GraphConverter graphToD3GraphConverter = new GraphToD3GraphConverter();

        objectMapper.writeValue(System.out, graphToD3GraphConverter.convert(graph));

    }

    private static String getAuthorEmail(RevCommit commit) {
        return commit.getAuthorIdent().getEmailAddress();
    }

    private static Set<GraphNode> getParentCommitHashes(RevCommit commit) {
        return Arrays.stream(commit.getParents())
                .map(parentCommit -> GraphNode.of(getCommitHash(parentCommit), getAuthorEmail(parentCommit)))
                .collect(Collectors.toSet());
    }

    private static String getCommitHash(RevCommit parentCommit) {
        return ObjectId.toString(parentCommit);
    }

    private static Set<String> getStickyNotesLabels(Set<Ref> branchNames) {
        return branchNames.stream()
                .map(Ref::getName)
                .filter(name -> name.startsWith("refs/heads"))
                .map(name -> name.substring("refs/heads".length() + 1))
                .collect(Collectors.toSet());
    }
}
