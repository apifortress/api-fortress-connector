/**
 * (c) 20013-2016 API Fortress, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1, a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.apifortress.config;

import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.components.Configuration;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.display.Summary;
import org.mule.api.annotations.param.Default;

/**
 * The global configuration
 * @author Simone Pezzano - simone@apifortress.com
 *
 */
@Configuration(friendlyName = "Configuration",configElementName="config")
public class ConnectorConfig {
   
    
    /**
     * max number of simultaneous connections
     */
    @Configurable
    @Default("2")
    @Summary("Number of symultaneous outbound HTTP connections")
    @Placement(tab="General",group="Connection")
    private int totalConnections;

    /**
     * Timeout during connection in seconds
     */
    @Configurable
    @Default("5")
    @Summary("Connect timeout in seconds")
    @Placement(tab="General",group="Connection")
    private int connectTimeout;

    /**
     * Socket timeout in seconds
     */
    @Configurable
    @Default("30")
    @Summary("Socket timeout in seconds")
    @Placement(tab="General",group="Connection")
    private int socketTimeout;
    
    /**
     * the threshold multiplier. The connector counts the test requests and accepts them only when the counter
     * is a multiple of this value. This is done to relax the sampling rate 
     */
    @Configurable
    @Default("1")
    @Placement(tab="Testing",group="Behavior")
    private int threshold;

    /**
     * When set to true, no notification will be triggered
     */
    @Configurable
    @Default("false")
    @Summary("The test execution will not trigger any notification")
    @Placement(tab="Testing",group="Behavior")
    private boolean silent;

    /**
     * When set to true, events won't be saved in API Fortress
     */
    @Configurable
    @Default("false")
    @Summary("No events will be stored in API Fortress. Use it in conjunction with 'synchronous'")
    @Placement(tab="Testing",group="Behavior")
    private boolean dryRun;
    
    public ConnectorConfig(){
        super();
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getTotalConnections() {
        return totalConnections;
    }

    public void setTotalConnections(int totalConnections) {
        this.totalConnections = totalConnections;
    }


    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }
    
}