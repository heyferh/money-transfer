package com.heyferh.test;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import metrics_influxdb.InfluxdbReporter;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableMetrics
public class MetricsConfig extends MetricsConfigurerAdapter {
    @Override
    public void configureReporters(MetricRegistry metricRegistry) {
        ScheduledReporter reporter = InfluxdbReporter.forRegistry(metricRegistry).build();
        reporter.start(5, TimeUnit.SECONDS);
    }
}
