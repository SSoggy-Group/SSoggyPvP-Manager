🎯 **What:** The testing gap addressed
The `YamlUtil.getSafeFile` method lacked error testing, specifically for cases where path traversal strings like '../config.yml' are provided.

📊 **Coverage:** What scenarios are now tested
Test cases have been added to verify that `YamlUtil.loadSection` correctly handles and blocks path traversal attempts by returning null. Similarly, `YamlUtil.saveConfig` has been tested to assure it safely rejects inputs attempting to write outside the configured data folder. Additionally, null safety for `dataFolder` and `filename` has been tested.

✨ **Result:** The improvement in test coverage
The error paths and null guarding within `YamlUtil` now have specific tests, increasing overall code reliability and testing coverage for filesystem operations.
