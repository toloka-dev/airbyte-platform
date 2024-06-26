/*
 * Copyright (c) 2020-2024 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.config.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.airbyte.config.Configs;
import io.airbyte.config.Configs.WorkerEnvironment;
import io.airbyte.config.storage.LocalStorageConfig;
import io.airbyte.featureflag.FeatureFlagClient;
import io.airbyte.featureflag.TestClient;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LogClientSingletonTest {

  private Configs configs;
  private CloudLogs mockLogClient;
  private LogConfigs logConfigs;
  private LocalStorageConfig storageConfig;

  private FeatureFlagClient featureFlagClient;

  @BeforeEach
  void setup() {
    configs = mock(Configs.class);
    mockLogClient = mock(CloudLogs.class);
    logConfigs = mock(LogConfigs.class);
    storageConfig = mock(LocalStorageConfig.class);
    featureFlagClient = new TestClient();
    LogClientSingleton.getInstance().logClient = mockLogClient;
  }

  @Test
  void testGetJobLogFileK8s() throws IOException {
    when(configs.getWorkerEnvironment()).thenReturn(WorkerEnvironment.KUBERNETES);
    when(configs.getLogConfigs()).thenReturn(logConfigs);
    when(logConfigs.getStorageConfig()).thenReturn(storageConfig);
    assertEquals(Collections.emptyList(),
        LogClientSingleton.getInstance().getJobLogFile(configs.getWorkerEnvironment(), configs.getLogConfigs(), Path.of("/job/1"),
            featureFlagClient));
    verify(mockLogClient).tailCloudLog(any(), eq("job-logging/job/1"), eq(LogClientSingleton.LOG_TAIL_SIZE), eq(featureFlagClient));
  }

  @Test
  void testGetJobLogFileNullPath() throws IOException {
    when(configs.getWorkerEnvironment()).thenReturn(WorkerEnvironment.KUBERNETES);
    when(configs.getLogConfigs()).thenReturn(logConfigs);
    when(logConfigs.getStorageConfig()).thenReturn(storageConfig);
    assertEquals(Collections.emptyList(),
        LogClientSingleton.getInstance().getJobLogFile(configs.getWorkerEnvironment(), configs.getLogConfigs(), null, featureFlagClient));
    verifyNoInteractions(mockLogClient);
  }

  @Test
  void testGetJobLogFileEmptyPath() throws IOException {
    when(configs.getWorkerEnvironment()).thenReturn(WorkerEnvironment.KUBERNETES);
    when(configs.getLogConfigs()).thenReturn(logConfigs);
    when(logConfigs.getStorageConfig()).thenReturn(storageConfig);
    assertEquals(Collections.emptyList(),
        LogClientSingleton.getInstance().getJobLogFile(configs.getWorkerEnvironment(), configs.getLogConfigs(), Path.of(""), featureFlagClient));
    verifyNoInteractions(mockLogClient);
  }

}
