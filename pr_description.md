🧪 [testing] Add unit tests for CommandUtil

🎯 **What:** Added missing unit test coverage for `CommandUtil.java`, specifically testing the `requirePlayer` method.
📊 **Coverage:** Covered both successful execution when a `Player` sender is passed and failure scenarios (returning `null` and sending color-translated error messages) when a non-Player `CommandSender` is passed.
✨ **Result:** Increased codebase reliability by ensuring the core utility method `requirePlayer` handles different implementations of Bukkit's `CommandSender` correctly as expected without regressions.
