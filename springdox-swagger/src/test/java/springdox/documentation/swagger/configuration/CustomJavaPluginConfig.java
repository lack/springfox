package springdox.documentation.swagger.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springdox.documentation.builders.ApiInfoBuilder;
import springdox.documentation.service.ApiInfo;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spring.web.plugins.Docket;
import springdox.documentation.swagger.annotations.EnableSwagger;

import static springdox.documentation.builders.PathSelectors.regex;


@Configuration
@EnableWebMvc
@EnableSwagger
@ComponentScan("springdox.documentation.spring.web.dummy") //Scan some controllers
public class CustomJavaPluginConfig {

  /**
   * Every SwaggerSpringMvcPlugin bean is picked up by the swagger-mvc framework - allowing for multiple
   * swagger groups i.e. same code base multiple swagger resource listings
   */
  @Bean
  public Docket customImplementation() {
    return new Docket(DocumentationType.SWAGGER_12)
            .groupName("customPlugin")
            .select()
              .paths(regex(".*pet.*"))
              .build();
  }

  @Bean
  public Docket secondCustomImplementation() {
    return new Docket(DocumentationType.SWAGGER_12)
            .groupName("secondCustomPlugin")
            .apiInfo(apiInfo())
            .select()
              .paths(regex("/feature.*"))
              .build();
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  private ApiInfo apiInfo() {
    ApiInfo apiInfo = new ApiInfoBuilder().title("My Apps API Title").description("My Apps API Description")
            .termsOfServiceUrl("My Apps API terms of service").contact("My Apps API Contact Email").license("My Apps " +
                    "API Licence Type").licenseUrl("My Apps API License URL").build();
    return apiInfo;
  }
}