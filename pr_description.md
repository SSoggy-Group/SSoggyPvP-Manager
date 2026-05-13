🧪 Add tests for PlayerListener

**What:** The testing gap addressed
This PR introduces unit tests for `PlayerListener`, specifically focusing on `onPlayerJoin` and `onPlayerQuit` event handlers. The test suite comprehensively ensures these methods correctly process data from `PvPManager` and adequately interact with the Bukkit scheduler.

**Coverage:** What scenarios are now tested
- `onPlayerJoin`:
  - Player joins without any PvP debt.
  - Player joins with PvP debt but has the `pvptoggle.bypass` permission.
  - Player joins with PvP debt and no bypass permission, checking task scheduling.
  - Verifies the scheduled task handles both offline and online states properly (e.g., verifying `sendMessage` when online).
- `onPlayerQuit`:
  - Ensuring that `PvPManager.requestSave()` is successfully invoked when a player quits, avoiding data loss/debt dodging.

**Result:** The improvement in test coverage
We now have robust unit test coverage on `PlayerListener`, verifying interactions with Bukkit's Scheduler and `PvPManager`. This will provide a safety net for any future refactoring regarding player join or quit states.
