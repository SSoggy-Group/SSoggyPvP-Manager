🎯 **What:**
Added unit tests for the `isInForcedPvPZone` method in `ZoneManager.java`. This method determines if a given location is inside any of the configured PvP zones, but it lacked dedicated tests to ensure its logic correctly checked null inputs, empty zones, inside/outside location logic, and cross-world interactions.

📊 **Coverage:**
We added the following test scenarios:
- `testIsInForcedPvPZone_NullLocation`: Verifies it returns false for a null Location.
- `testIsInForcedPvPZone_NullWorld`: Verifies it returns false for a Location with a null World.
- `testIsInForcedPvPZone_EmptyZones`: Verifies it returns false when there are no configured zones.
- `testIsInForcedPvPZone_InZone`: Verifies it returns true when the location is inside a created zone.
- `testIsInForcedPvPZone_OutsideZone`: Verifies it returns false when the location is outside all created zones.
- `testIsInForcedPvPZone_DifferentWorld`: Verifies it returns false when the location is in the same coordinates but a different world.

✨ **Result:**
The testing gap is addressed. The tests have passed successfully. `ZoneManager` can be safely modified without the fear of breaking the forced PvP zone lookup behavior.
