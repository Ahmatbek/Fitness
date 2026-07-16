//package kg.biamino.projects.config;
//
//
//
//import jakarta.servlet.*;
//import org.jspecify.annotations.Nullable;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.CharacterEncodingFilter;
//import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
//
//public class WebConfig extends AbstractAnnotationConfigDispatcherServletInitializer {
//
//
//    @Override
//    protected Class<?> @Nullable [] getRootConfigClasses() {
//        return new Class[]{AppConfig.class};
//    }
//
//    @Override
//    protected Class<?> @Nullable [] getServletConfigClasses() {
//        return new Class[]{ControllerConfig.class};
//    }
//
//    @Override
//    protected String[] getServletMappings() {
//        return new String[]{"/"};
//    }
//
//    @Override
//    protected Filter[] getServletFilters() {
//        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
//        characterEncodingFilter.setEncoding("UTF-8");
//        characterEncodingFilter.setForceEncoding(true);
//        return new Filter[]{characterEncodingFilter};
//    }
//}
