package kg.biamino.projects;

import jakarta.servlet.*;
import kg.biamino.projects.config.AppConfig;
import kg.biamino.projects.config.ControllerConfig;
import kg.biamino.projects.dto.*;
import kg.biamino.projects.records.ProfilePasswordChange;
import kg.biamino.projects.records.TraineeCriteriaDto;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.hibernate.boot.models.annotations.internal.FilterDefAnnotation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;
import java.time.LocalDate;
public class Main {
    public static void main(String[] args) throws LifecycleException {

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.setBaseDir(System.getProperty("java.io.tmpdir"));
        tomcat.getConnector();

        Context context = tomcat.addContext("", new File(".").getAbsolutePath());

        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(AppConfig.class);
        rootContext.refresh();

        AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
        webContext.register(ControllerConfig.class);

        webContext.setParent(rootContext);


//        webContext.setServletContext( context.getServletContext());

        Tomcat.addServlet(context, "dispatcher", new DispatcherServlet(webContext));
        context.addServletMappingDecoded("/", "dispatcher");






        FilterDef filterDef = new FilterDef();
        filterDef.setFilter(new CharacterEncodingFilter("UTF-8", true));
        filterDef.setFilterName("encodingFilter");
        context.addFilterDef(filterDef);




        FilterMap filterMap = new FilterMap();
        filterMap.addURLPattern("/*");
        filterMap.setFilterName("encodingFilter");
        context.addFilterMap(filterMap);











        tomcat.start();
        tomcat.getServer().await();










    }

}