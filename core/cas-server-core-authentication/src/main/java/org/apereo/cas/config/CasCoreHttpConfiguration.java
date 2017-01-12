package org.apereo.cas.config;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apereo.cas.authentication.FileTrustStoreSslSocketFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.util.http.HttpClient;
import org.apereo.cas.util.http.SimpleHttpClientFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * This is {@link CasCoreHttpConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Configuration("casCoreHttpConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@AutoConfigureBefore(CasCoreAuthenticationConfiguration.class)
public class CasCoreHttpConfiguration {
    
    @Autowired
    private CasConfigurationProperties casProperties;
    
    @RefreshScope
    @Bean
    public SSLConnectionSocketFactory trustStoreSslSocketFactory() {
        return new FileTrustStoreSslSocketFactory(casProperties.getHttpClient().getTruststore().getFile(),
                casProperties.getHttpClient().getTruststore().getPsw());
    }

    @Bean
    public SimpleHttpClientFactoryBean.DefaultHttpClient httpClient() {
        final SimpleHttpClientFactoryBean.DefaultHttpClient c = new SimpleHttpClientFactoryBean.DefaultHttpClient();
        c.setConnectionTimeout(casProperties.getHttpClient().getConnectionTimeout());
        c.setReadTimeout(Long.valueOf(casProperties.getHttpClient().getReadTimeout()).intValue());
        return c;
    }

    @Bean
    public HttpClient noRedirectHttpClient() throws Exception {
        final SimpleHttpClientFactoryBean.DefaultHttpClient c = new SimpleHttpClientFactoryBean.DefaultHttpClient();
        c.setConnectionTimeout(casProperties.getHttpClient().getConnectionTimeout());
        c.setReadTimeout(Long.valueOf(casProperties.getHttpClient().getReadTimeout()).intValue());
        c.setRedirectsEnabled(false);
        c.setCircularRedirectsAllowed(false);
        c.setSslSocketFactory(trustStoreSslSocketFactory());
        return c.getObject();
    }

    @Bean
    public HttpClient supportsTrustStoreSslSocketFactoryHttpClient() throws Exception {
        final SimpleHttpClientFactoryBean.DefaultHttpClient c = new SimpleHttpClientFactoryBean.DefaultHttpClient();
        c.setConnectionTimeout(casProperties.getHttpClient().getConnectionTimeout());
        c.setReadTimeout(Long.valueOf(casProperties.getHttpClient().getReadTimeout()).intValue());
        c.setSslSocketFactory(trustStoreSslSocketFactory());
        return c.getObject();
    }
}
