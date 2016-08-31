/*
 * This file is part of Flying PhotoBooth.
 * 
 * Flying PhotoBooth is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Flying PhotoBooth is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Flying PhotoBooth.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.groundupworks.flyingphotobooth;

import android.support.annotation.Nullable;
import com.groundupworks.flyingphotobooth.client.ServiceClient;
import com.groundupworks.flyingphotobooth.client.ServiceGenerator;
import com.groundupworks.lib.photobooth.framework.BaseApplication;

/**
 * A concrete {@link BaseApplication} class.
 *
 * @author Benedict Lau
 */
public class MyApplication extends BaseApplication {
    private ServiceClient serviceClient;

    @Override
    public void onCreate() {
        super.onCreate();
        serviceClient = ServiceGenerator.createService(ServiceClient.class);
    }

    @Nullable
    public ServiceClient getServiceClient() {
        return serviceClient;
    }
}
