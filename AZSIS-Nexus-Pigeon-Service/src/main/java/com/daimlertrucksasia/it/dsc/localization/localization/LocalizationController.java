package com.daimlertrucksasia.it.dsc.localization.localization;

import com.daimlertrucksasia.it.dsc.localization.localization.infra.MessageRepository;
import com.daimlertrucksasia.it.dsc.localization.localization.model.LocalizedMessageEntity;
import com.daimlertrucksasia.it.dsc.localization.localization.model.LocalizedMsgRequest;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * GraphQL controller for managing localized message templates.
 * <p>
 * This controller exposes mutation operations through a GraphQL API for registering
 * localized message templates that are used throughout the application.
 * </p>
 *
 * <p>
 * An instance of {@link MessageRepository} is injected to persist localized message entities
 * to the underlying MongoDB datastore.
 * </p>
 *
 * <p>
 * This class is annotated with {@link GraphQLApi} and {@link Component} to expose GraphQL APIs
 * via Spring Boot using SPQR integration.
 * </p>
 */
@GraphQLApi
@Component
public class LocalizationController {

    private final MessageRepository messageRepo;

    /**
     * Constructs a {@code LocalizationController} with the given {@link MessageRepository}.
     *
     * @param messageRepo the repository used to interact with the message data store
     */
    @Autowired
    public LocalizationController(MessageRepository messageRepo) {
        this.messageRepo = messageRepo;
    }


    /**
     * Performs a basic health check of the GraphQL API.
     * <p>
     * This query can be used to verify that the GraphQL schema is valid and the API is accessible.
     * It is particularly useful for troubleshooting deployment or integration issues, and also
     * ensures that the schema includes at least one field in the Query root, as required by
     * the GraphQL specification.
     * </p>
     *
     * @return a static message indicating that the GraphQL schema is functioning correctly
     */
    @GraphQLQuery(name = "healthCheck", description = "Simple ping to verify the schema has a Query root")
    public String healthCheck() {
        return "GraphQL schema is valid and working.";
    }

    /**
     * Registers a new localized message template in the system.
     *
     * <p>
     * This GraphQL mutation accepts a {@link LocalizedMsgRequest} input that includes
     * the locale, message content, and template ID. It returns the saved {@link LocalizedMessageEntity}.
     * </p>
     *
     * <p>
     * Note: This mutation is marked as deprecated with the reason "MSG template Registration",
     * which may indicate a planned change or migration in how templates are registered.
     * </p>
     *
     * @param newMsg the new localized message template to be created (must not be null and must be valid)
     * @return the persisted {@link LocalizedMessageEntity}
     */
    @GraphQLMutation(name = "createMsgTemplate", deprecationReason = "MSG template Registration")
    public LocalizedMessageEntity createLocalizedMsg(@GraphQLArgument(name = "newMsg") @GraphQLNonNull @Valid LocalizedMsgRequest newMsg) {
        return messageRepo.save(LocalizedMessageEntity.builder()
                .locale(newMsg.getLocale())
                .message(newMsg.getMessage())
                .msgTemplateID(newMsg.getMsgTemplateID())
                .serviceProviderID(newMsg.getServiceProviderID())
                .serviceConsumerID(newMsg.getServiceConsumerID())
                .build());
    }
}
