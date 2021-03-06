/*
 * Copyright (c) 2015 Nexmo Inc
 * All rights reserved.
 *
 * Licensed only under the Nexmo Verify SDK License Agreement located at
 *
 * https://www.nexmo.com/terms-use/verify-sdk/ (the “License”)
 *
 * You may not use, exercise any rights with respect to or exploit this SDK,
 * or any modifications or derivative works thereof, except in accordance
 * with the License.
 */

package com.nexmo.sdk;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.nexmo.sdk.core.client.ClientBuilderException;
import com.nexmo.sdk.core.config.Config;
import com.nexmo.sdk.verify.core.request.VerifyRequest;
import com.nexmo.sdk.verify.core.service.BaseService;

/**
 * The {@link com.nexmo.sdk.NexmoClient} is the Nexmo SDK entry point.
 * It provides access to the Verify mechanism acquired through a {@link com.nexmo.sdk.verify.client.VerifyClient} object.
 * For the security of your Nexmo account, you should not embed your sharedSecretKey or your
 * Nexmo authorization token as strings in the app you submit to the Google Play Store.
 *
 * <p>To construct a NexmoClient, the required parameters are:
 * <ul>
 *     <li>applicationContext: The application context.</li>
 *     <li>sharedSecretKey:    The pre-shared secret key generated by Nexmo, visible in your user Dashboard.
 *     This key is unique for each application registered to use the Number SDK service.</li>
 *     <li>applicationId:      The auto-generated id for the application.</li>
 * </ul>
 * <p>Optional parameter, if you wish to integrate push notifications to your app and receive the PIN code via push.
 * <ul>
 *     <li>gcmSenderId:       Your Google API project number used for GoogleCloudMessaging. Aquire this project number from Google Developer
 *                            Console, as described in <a href="https://developers.google.com/cloud-messaging/"></a>Getting Started.</li>
 * </ul>
 * If the required parameters for creating a {@link com.nexmo.sdk.NexmoClient} instance are not supplied, then {@link NexmoClientBuilder#build} fails by throwing a {@link ClientBuilderException} exception.
 * <p> Example usage:
 * <pre>
 *     try{
 *         // Create a NexmoClient using the NexmoClientBuilder.
 *         NexmoClient myNexmoClient = new NexmoClient.NexmoClientBuilder()
 *                                      .context(myAppContext)
 *                                      .sharedSecretKey("...")
 *                                      .applicationId("...")
 *                                      .build();
 *     } catch (ClientBuilderException e) {
 *         e.printStackTrace();
 *     }
 * </pre>
 */
public class NexmoClient implements Parcelable {

    /** Environment endpoint: production or sandbox. */
    public enum ENVIRONMENT_HOST {
        /** Used for applications deployed in production. */
        PRODUCTION,
        /** Used during development and testing. This functionality is not available yet in the SDK.*/
        SANDBOX
    }

    private Context context;
    private final String appId;
    private final String sharedSecretKey;
    private String environmentHost;
    private String GcmRegistrationToken;

    private NexmoClient(final Context context, final String appId, final String secretKey, final String environmentHost, final String GcmRegistrationToken) {
        this.context = context;
        this.appId = appId;
        this.sharedSecretKey = secretKey;
        this.environmentHost = environmentHost;
        this.GcmRegistrationToken = GcmRegistrationToken;
    }

    private NexmoClient(final Context context, final String appId, final String secretKey, final ENVIRONMENT_HOST environmentHost, final String GcmRegistrationToken) {
        this(context, appId, secretKey, (environmentHost == ENVIRONMENT_HOST.PRODUCTION ? Config.ENDPOINT_PRODUCTION : null), GcmRegistrationToken);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<NexmoClient> CREATOR
            = new Parcelable.Creator<NexmoClient>() {
        public NexmoClient createFromParcel(Parcel in) {
            return new NexmoClient(in);
        }

        public NexmoClient[] newArray(int size) {
            return new NexmoClient[size];
        }
    };

    /**
     * Reset the GCM registration token whenever there is a new one available.
     * @param gcmRegistrationToken The GCM registration ID that uniquely identifies an app/device pairing for push purposes.
     */
    public void setGcmRegistrationToken(final String gcmRegistrationToken) {
        synchronized(this) {
            this.GcmRegistrationToken = gcmRegistrationToken;
        }
    }

    /**
     * Returns the context used for this {@link com.nexmo.sdk.NexmoClient} instance.
     * @return The context.
     */
    public final Context getContext() {
        return this.context;
    }

    /**
     * Returns the pre-shared secret key generated by Nexmo.
     * @return The pre-shared secret key.
     */
    public final String getSharedSecretKey() {
        return this.sharedSecretKey;
    }

    /**
     * Returns the application id used for this {@link com.nexmo.sdk.NexmoClient} instance.
     * @return The auto-generated id for the application.
     */
    public final String getApplicationId() {
        return this.appId;
    }

    /**
     * Returns the host used for this {@link com.nexmo.sdk.NexmoClient} instance.
     * @return The host, by default {@link com.nexmo.sdk.core.config.Config#ENDPOINT_PRODUCTION} is available for use.
     */
    public final String getEnvironmentHost() {
        return this.environmentHost;
    }

    /**
     * Returns the provided GCM registration token value for this project.
     * This value should be set prior to any verification start......
     * @return  The GCM registration ID that uniquely identifies an app/device pairing for push purposes.
     */
    public String getGcmRegistrationToken() {
        return this.GcmRegistrationToken;
    }

    /**
     * Returns the current version of the Nexmo SDK library.
     *
     *  @return The current version of the Nexmo SDK library.
     */
    public static String getVersion() {
        return Config.SDK_REVISION_CODE;
    }

    @Override
    public String toString() {
        return "ApplicationId: " + (this.appId != null ? this.appId : "") + "," + "SharedKey: " + (this.sharedSecretKey != null ? this.sharedSecretKey : "") + "," +
                "Environment: " + (this.environmentHost != null ? this.environmentHost : "") + "," + "GcmRegistrationToken: " + (this.GcmRegistrationToken != null ? this.GcmRegistrationToken : "");
    }

    /**
     * Build a new {@link NexmoClient} instance, based on the following mandatory parameters:
     * <ul>
     *     <li>applicationContext: The application context.</li>
     *     <li>sharedSecretKey:    The pre-shared secret key generated by Nexmo, visible in your user Dashboard.
     *     This key is unique for each application registered to use the Number SDK service.</li>
     *     <li>applicationId:      The auto-generated id for the application.</li>
     * </ul>
     * If the required parameters are not supplied an instance cannot be created, and fails by throwing a {@link ClientBuilderException} exception.
     * <p> Example usage:
     * <pre>
     *     try{
     *         // Create a NexmoClient using the NexmoClientBuilder.
     *         NexmoClient myNexmoClient = new NexmoClient.NexmoClientBuilder()
     *                                      .context(myAppContext)
     *                                      .sharedSecretKey("...")
     *                                      .applicationId("...")
     *                                      .GcmRegistrationId("...") // optional, for push integration only.
     *                                      .build();
     *     } catch (ClientBuilderException e) {
     *         e.printStackTrace();
     *     }
     * </pre>
     */
    public static class NexmoClientBuilder {

        private Context context;
        private String appId;
        private String sharedSecretKey;
        private String environmentHost = Config.ENDPOINT_PRODUCTION;
        private String GcmRegistrationToken;

        /**
         * Acquire a NexmoClient, based on the following mandatory parameters:
         * <ul>
         * <li>applicationContext: The application context.</li>
         * <li>sharedSecretKey:    The pre-shared secret key generated by Nexmo, visible in your user Dashboard.
         * This key is unique for each application registered to use the Number SDK service.</li>
         * <li>applicationId:      The auto-generated id for the application.</li>
         * </ul>
         * If the required parameters are not supplied an instance cannot be created, and fails by throwing a {@link ClientBuilderException} exception.
         *
         * @return an instance of {@link com.nexmo.sdk.NexmoClient}.
         * @throws ClientBuilderException a {@link com.nexmo.sdk.core.client.ClientBuilderException}.
         */
        public NexmoClient build() throws ClientBuilderException {
            StringBuilder stringBuilder = new StringBuilder();
            if(this.context == null)
                ClientBuilderException.appendExceptionCause(stringBuilder, "Context");
            if(TextUtils.isEmpty(this.sharedSecretKey))
                ClientBuilderException.appendExceptionCause(stringBuilder, BaseService.RESPONSE_SIG);
            if(TextUtils.isEmpty(this.appId))
                ClientBuilderException.appendExceptionCause(stringBuilder, BaseService.PARAM_APP_ID);
            if(TextUtils.isEmpty(this.environmentHost))
                ClientBuilderException.appendExceptionCause(stringBuilder, "environmentHost");

            String missingParameters = stringBuilder.toString();
            if(!TextUtils.isEmpty(missingParameters))
                throw new ClientBuilderException("Building a NexmoClient instance has failed due to missing parameters: " + missingParameters);
            else
                return new NexmoClient(this.context, this.appId, this.sharedSecretKey, this.environmentHost, this.GcmRegistrationToken);
        }

        public NexmoClientBuilder context(final Context context) {
            this.context = context;
            return this;
        }

        public NexmoClientBuilder applicationId(final String applicationId) {
            this.appId = applicationId;
            return this;
        }

        public NexmoClientBuilder sharedSecretKey(final String sharedSecretKey) {
            this.sharedSecretKey = sharedSecretKey;
            return this;
        }

        public NexmoClientBuilder environmentHost(final String environmentHost) {
            this.environmentHost = environmentHost;
            return this;
        }

        public NexmoClientBuilder environmentHost(final ENVIRONMENT_HOST environmentHost) {
            this.environmentHost = environmentHost == ENVIRONMENT_HOST.PRODUCTION ? Config.ENDPOINT_PRODUCTION : null;
            return this;
        }

        public NexmoClientBuilder gcmRegistrationToken(final String GcmRegistrationToken) {
            this.GcmRegistrationToken = GcmRegistrationToken;
            return this;
        }
    }

    private NexmoClient(Parcel input) {
        this.appId = input.readString();
        this.sharedSecretKey = input.readString();
        this.environmentHost = input.readString();
        this.GcmRegistrationToken = input.readString();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.appId);
        out.writeString(this.sharedSecretKey);
        out.writeString(this.environmentHost);
        out.writeString(this.GcmRegistrationToken);
    }

}
