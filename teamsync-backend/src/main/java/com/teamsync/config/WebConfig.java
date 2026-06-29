package com.teamsync.config;

import com.teamsync.interceptor.JwtInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private String getAbsoluteUploadPath() {
        String path = uploadDir;
        if (path.startsWith("~" + System.getProperty("file.separator"))) {
            path = System.getProperty("user.home") + path.substring(1);
        } else if (path.startsWith("~")) {
            path = System.getProperty("user.home") + path.substring(1);
        }
        return path;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/documents/preview/**",
                        "/api/documents/download/**"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String resolvedPath = getAbsoluteUploadPath();
        Path absoluteUploadPath = Paths.get(resolvedPath).toAbsolutePath().normalize();
        String resourceLocation = "file:" + absoluteUploadPath.toString() + "/";
        logger.info("配置文件上传资源路径: {}", resourceLocation);
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation);
    }
}
