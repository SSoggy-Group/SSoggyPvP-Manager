---
layout: default
title: Playtime System
description: Understand how playtime debt forces PvP after configured playtime cycles
---

SSoggyPvP-Manager can force players into PvP after they accumulate a configured amount of playtime.

## Configuration

```yaml
playtime:
  minutes-per-cycle: 60
  hours-per-cycle: 1
  forced-seconds: 1200
  forced-minutes: 20
  solo-accumulate: true
  solo-forced: false
```

With the default values:

- each 60 minutes of playtime creates a cycle
- each cycle adds 1200 seconds (20 minutes) of forced PvP debt

## How It Works

1. A player accumulates normal playtime while online. By default this happens even when alone; set `solo-accumulate: false` to require 2+ players online before playtime counts.
2. When their total playtime crosses a configured cycle threshold (`minutes-per-cycle` or `hours-per-cycle`), the plugin records a processed cycle.
3. The player gains forced PvP debt for that cycle (`forced-seconds` or `forced-minutes`).
4. While debt is active, PvP cannot be turned off.
5. Debt counts down according to the `solo-forced` configuration (by default, only when at least 2 players are online).
6. Logging out does not clear debt.

## Player Experience

When debt is active:

- `/pvp off` is blocked
- the player sees the configured forced-playtime message
- `/pvp status` shows the remaining debt time

Config message example:

```yaml
messages:
  pvp-forced-playtime: "&4&lForced PvP active! &f%time% &cremaining."
```

## Bypass Behavior

Players with `pvptoggle.bypass` are exempt from playtime-based forced PvP.

## Admin Inspection

To inspect an affected player:

```bash
/pvpadmin player Steve info
```

To set or clear debt manually:

```bash
/pvpadmin player Steve setdebt 1200
/pvpadmin player Steve setdebt 0
```
