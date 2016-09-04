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
package com.arthurtimberly;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import com.arthurtimberly.client.ServiceClient;
import com.arthurtimberly.client.ServiceGenerator;
import com.groundupworks.lib.photobooth.framework.BaseApplication;

import java.util.Random;

/**
 * A concrete {@link BaseApplication} class.
 */
public class MyApplication extends BaseApplication {
    private ServiceClient serviceClient;
    private Random random;

    @Override
    public void onCreate() {
        super.onCreate();
        serviceClient = ServiceGenerator.createService(ServiceClient.class);
        random = new Random();
    }

    @NonNull
    public ServiceClient getServiceClient() {
        return serviceClient;
    }

    @IntRange(from=100000, to=999999)
    public long getSixDigitUniqueId() {
        return 100000 + random.nextInt(900000);
    }
}
