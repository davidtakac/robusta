# Robusta - keep your screen awake

Simple quick settings app that keeps your screen awake. 

## Features

- No user interface
- Quick settings tile for keeping your screen awake, but allowing it to dim, therefore 
conserving battery
- Quick settings tile for keeping your screen fully awake, therefore harder on the battery
- Permanent status notification **when running**
- Automatically stops when the screen is turned off (e.g. when you lock your phone)
- Stop action in the permanent notification

## Instructions

1. Install the app
2. Add the tile you need to your quick settings
3. Click the tile 
4. A permanent notification with a stop action shows
5. Click the stop action or turn the screen off manually to stop 
6. Repeat

## Battery

The app itself doesn't do any battery-draining work. But keeping your screen on for long 
periods of time will drain the battery faster. You can mitigate this by using the option 
that allows screen dimming. 

## How it works

When you click the quick settings tile, the application starts a background service 
which acquires a [wakelock](https://developer.android.com/training/scheduling/wakelock#cpu). 

The background service is promoted to a foreground service which displays the notification 
and discourages the system from killing it. As long as the service is alive, the wakelock 
is acquired and your screen doesn't turn off. 

## Why 

Robusta is a type of coffee bean. 

Inspired by [LineageOS](https://lineageos.org/) "Caffeine" quick settings tile. 

Most apps and devices today are using a better way of [keeping the screen on](https://developer.android.com/training/scheduling/wakelock#screen). This app is a crutch for when such 
features aren't available. 

## Requirements 

Android 7.0 and up