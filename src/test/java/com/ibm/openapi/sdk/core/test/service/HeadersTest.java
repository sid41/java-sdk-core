/**
 * Copyright 2018 IBM Corp. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.ibm.openapi.sdk.core.test.service;

import com.ibm.openapi.sdk.core.http.HttpMediaType;
import com.ibm.openapi.sdk.core.http.RequestBuilder;
import com.ibm.openapi.sdk.core.http.ServiceCall;
import com.ibm.openapi.sdk.core.service.WatsonService;
import com.ibm.openapi.sdk.core.service.model.GenericModel;
import com.ibm.openapi.sdk.core.test.WatsonServiceUnitTest;
import com.ibm.openapi.sdk.core.util.ResponseConverterUtils;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class HeadersTest extends WatsonServiceUnitTest {

  private class TestModel extends GenericModel { }

  public class TestService extends WatsonService {

    private static final String SERVICE_NAME = "test";

    public TestService() {
      super(SERVICE_NAME);
    }

    public ServiceCall<TestModel> testMethod() {
      RequestBuilder builder = RequestBuilder.get(HttpUrl.parse(getEndPoint() + "/v1/test"));
      return createServiceCall(builder.build(), ResponseConverterUtils.getObject(TestModel.class));
    }
  }

  private TestService service;

  /*
   * (non-Javadoc)
   *
   * @see com.ibm.openapi.sdk.core.test.WatsonServiceTest#setUp()
   */
  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    service = new TestService();
    service.setUsernameAndPassword("", "");
    service.setEndPoint(getMockWebServerUrl());
  }

  /**
   * Test adding a custom header to a request.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAddHeader() throws InterruptedException {
    server.enqueue(new MockResponse()
        .addHeader(CONTENT_TYPE, HttpMediaType.APPLICATION_JSON)
        .setBody("{\"test_key\": \"test_value\"}"));

    String headerName = "X-Test";
    service.testMethod()
        .addHeader(headerName, "test")
        .execute();
    final RecordedRequest request = server.takeRequest();
    assertTrue(request.getHeader(headerName) != null);
  }
}
