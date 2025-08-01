package com.daimlertrucksasia.it.dsc.localization.exceptions.e;

import graphql.GraphQL;
import io.leangen.graphql.spqr.spring.autoconfigure.BaseAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration class for customizing the {@link GraphQL} instance used by
 * the SPQR GraphQL library. This configuration replaces the default
 * {@code DataFetcherExceptionHandler} with a custom handler to provide
 * structured and meaningful error responses to GraphQL clients.
 *
 * <p>The class uses a {@link BeanPostProcessor} to intercept the
 * autoconfigured {@code GraphQL} bean and rebuild it using a custom
 * {@link CustomGraphQLExceptionHandler}.</p>
 *
 * <p>It imports {@link BaseAutoConfiguration} to ensure that all necessary
 * GraphQL infrastructure is loaded before applying the customization.</p>
 *
 * <p><b>Note:</b> This approach is used with SPQR GraphQL integration for
 * Spring Boot, typically relying on the <code>graphql-spqr-spring-boot-starter</code>.</p>
 *
 *
 * @since 1.0
 */
@Configuration
@Import(BaseAutoConfiguration.class)
public class GraphQLConfig {

    /**
     * Custom exception handler for GraphQL data fetching errors.
     */
    @Autowired
    private CustomGraphQLExceptionHandler exceptionHandler;

    /**
     * Registers a {@link BeanPostProcessor} that intercepts the GraphQL bean
     * after it is initialized and injects the custom exception handler.
     *
     * @return a BeanPostProcessor to rebuild the GraphQL instance
     *         with a custom DataFetcherExceptionHandler
     */
    @Bean
    public BeanPostProcessor graphQLBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                if (bean instanceof GraphQL && "graphQL".equals(beanName)) {
                    return GraphQL.newGraphQL(((GraphQL) bean).getGraphQLSchema())
                            .defaultDataFetcherExceptionHandler(exceptionHandler)
                            .build();
                }
                return bean;
            }
        };
    }
}
