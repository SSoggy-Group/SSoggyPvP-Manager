🧪 Add tests for CombatListener.onEntityDamageByEntity

🎯 **What:** Added comprehensive unit tests for `CombatListener.onEntityDamageByEntity` to ensure the combat rules are applied correctly.
📊 **Coverage:** The tests cover the following scenarios:
  - Non-player victim (early return).
  - Unresolvable attacker (early return).
  - Self-damage (early return).
  - Attacker has PvP disabled (event cancelled, message sent).
  - Victim has PvP disabled (event cancelled, message sent).
  - Both players have PvP enabled (event allowed).
  - Attacker resolution for direct player, projectile, and tameable entities.
✨ **Result:** Improved test coverage and confidence in the core PvP combat logic.
