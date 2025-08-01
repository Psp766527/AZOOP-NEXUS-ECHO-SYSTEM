package com.daimlertrucksasia.it.dsc.localization.exceptions.e;

import com.daimlertrucksasia.it.dsc.localization.kafka.service.producer.KafkaProducerService;
import com.daimlertrucksasia.it.dsc.localization.localization.service.MessageService;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;

import jakarta.validation.ValidationException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.concurrent.CompletableFuture;

import static io.leangen.graphql.util.ClassFinder.log;

/**
 * Custom GraphQL Exception Handler to centralize exception translation and provide localized, structured error responses.
 * <p>
 * This handler intercepts exceptions thrown from GraphQL resolvers and maps them to custom {@link GraphQLErrorResponse}
 * with relevant HTTP status, error codes, messages, and details.
 * <p>
 * Supported exceptions include:
 * - {@link ResourceNotFoundException}
 * - {@link ValidationException}
 * - {@link DataAccessException}
 * - Any {@link Throwable} (as fallback)
 * <p>
 * Error messages are localized using {@link MessageService} and logged appropriately.
 */
@Component
public class CustomGraphQLExceptionHandler implements DataFetcherExceptionHandler {

    /** Message service for localization */
    private final MessageService messageService;

    /** Kafka producer used for optional logging/event publishing (not actively used in this handler) */
    private final KafkaProducerService kafkaProducerService;

    /** Map to store exception-to-response translation handlers */
    private final Map<Class<? extends Throwable>, Function<DataFetcherExceptionHandlerParameters, GraphQLErrorResponse>> handlers;

    /**
     * Constructor to initialize exception handler mappings.
     *
     * @param messageService the message service for i18n support
     * @param kafkaProducerService Kafka producer for optional logging/notifications
     */
    public CustomGraphQLExceptionHandler(MessageService messageService, KafkaProducerService kafkaProducerService) {
        this.messageService = messageService;
        this.kafkaProducerService = kafkaProducerService;

        handlers = new HashMap<>();

        // Specific handler for ResourceNotFoundException
        handlers.put(ResourceNotFoundException.class, params -> GraphQLErrorResponse.builder()
                .message(params.getException().getMessage())
                .status(400)
                .errorCode("NOT_FOUND")
                .details("The requested resource was not found")
                .path(params.getPath().toList())
                .locations(List.of(params.getSourceLocation()))
                .timestamp(OffsetDateTime.now())
                .build());

        // Handler for bean validation exceptions
        handlers.put(ValidationException.class, params -> GraphQLErrorResponse.builder()
                .message(messageService.getMessage(
                        params.getException().getMessage() != null ? params.getException().getMessage() : "In-Valid GraphQL Query/Mutation",
                        Locale.ENGLISH))
                .status(400)
                .errorCode("VALIDATION_FAILED")
                .details("New Details")
                .path(params.getPath().toList())
                .locations(List.of(params.getSourceLocation()))
                .timestamp(OffsetDateTime.now())
                .build());

/*
        // Handler for MongoDB socket exceptions (e.g., connectivity issues)
        handlers.put(MongoSocketException.class, params -> GraphQLErrorResponse.builder()
                .message(messageService.getMessage(
                        params.getException().getMessage() != null ? params.getException().getMessage() : "Error while Executing DB Query",
                        Locale.ENGLISH))
                .status(400)
                .errorCode("VALIDATION_FAILED")
                .details("New Details")
                .path(params.getPath().toList())
                .locations(List.of(params.getSourceLocation()))
                .timestamp(OffsetDateTime.now())
                .build());
*/

        // Handler for generic PGSQL exceptions
        handlers.put(DataAccessException.class, params -> GraphQLErrorResponse.builder()
                .message(messageService.getMessage(
                        params.getException().getMessage() != null ? params.getException().getMessage() : "Error while Executing DB Query",
                        Locale.ENGLISH))
                .status(400)
                .errorCode("VALIDATION_FAILED")
                .details("New Details")
                .path(params.getPath().toList())
                .locations(List.of(params.getSourceLocation()))
                .timestamp(OffsetDateTime.now())
                .build());

        // Fallback handler for all other uncaught exceptions
        handlers.put(Throwable.class, params -> GraphQLErrorResponse.builder()
                .message(messageService.getMessage(
                        params.getException().getMessage() != null ? params.getException().getMessage() : "Internal Server Error",
                        Locale.ENGLISH))
                .status(400)
                .errorCode("INTERNAL_SERVER_ERROR")
                .details("Nothing to Show")
                .path(params.getPath().toList())
                .locations(List.of(params.getSourceLocation()))
                .timestamp(OffsetDateTime.now())
                .build());
    }

    /**
     * Handles exceptions thrown during GraphQL data fetching.
     *
     * Looks up the appropriate handler for the thrown exception and builds a {@link DataFetcherExceptionHandlerResult}
     * to return as a GraphQL error response.
     *
     * @param parameters the parameters including the exception and source location
     * @return a completed future containing the error result
     */
    @Override
    public CompletableFuture<DataFetcherExceptionHandlerResult> handleException(DataFetcherExceptionHandlerParameters parameters) {
        Throwable exception = parameters.getException();
        log.error("GraphQL error: {}", exception.getMessage(), exception);

        GraphQLErrorResponse error = handlers.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(exception))
                .findFirst()
                .map(entry -> entry.getValue().apply(parameters))
                .orElse(handlers.get(Throwable.class).apply(parameters)); // fallback

        DataFetcherExceptionHandlerResult result = DataFetcherExceptionHandlerResult.newResult()
                .error(error)
                .build();

        return CompletableFuture.completedFuture(result);
    }
}
