
Wedding PhotoBooth
=================

Create, review and share photos in realtime.

#### Capture Screen
* Start button
* Camera Revert button
* Once start, it should start taking pictures 6 times
* 5 seconds between the photo captures

#### Photo Picker Screen
* User can pick 4 out of 6 pictures
* Cancel or Okay Button (maybe FAB buttons)
* Choose different filter in a horizontal scrollview

#### Post capture
* Show the user the final picture with filtering and wedding background
* Background service uploads the photos to flickr api?
* Once the upload completes, write photo metadata to API
* Generate an ID (or a number) that uniquely associates with that section


Flying PhotoBooth
=================

The source code for the Android applications **Flying PhotoBooth** and **Party PhotoBooth**.

## Flying PhotoBooth <a href="https://play.google.com/store/apps/details?id=com.groundupworks.flyingphotobooth&utm_source=global_co&utm_medium=prtnr&utm_content=Mar2515&utm_campaign=PartBadge&pcampaignid=MKT-AC-global-none-all-co-pr-py-PartBadges-Oct1515-1"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png" height="24px" /></a>

Create, review, save and share photo strips all in less than 30 seconds.

#### Create Photo Strip

* Selection of photo strip arrangements and photo filters
* Shoot up to 4 photos manually or use the photo booth style timer
* Unique swipe-to-retake feature to quickly review each photo
* Support for triggering with Muku Shuttr, other Bluetooth remotes and keyboards

#### Basic Save and Share

* Photo strips are auto-saved and added to the Gallery
* Beam to compatible devices using Android Beam
* Share through Facebook, Twitter, WhatsApp, Email, etc.

#### Share with [Wings](http://www.groundupworks.com/wings/)

* Link your Facebook, Dropbox, and Google Cloud Print account to enable one-click or auto share/print
* Automatically schedule retries in the background if sharing fails
* Share to any of your Facebook Albums or Pages, with privacy level control

## Party PhotoBooth <a href="https://play.google.com/store/apps/details?id=com.groundupworks.partyphotobooth&utm_source=global_co&utm_medium=prtnr&utm_content=Mar2515&utm_campaign=PartBadge&pcampaignid=MKT-AC-global-none-all-co-pr-py-PartBadges-Oct1515-1"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png" height="24px" /></a>

Need a photo booth at your event? There is an app for that! What's a better way to explain how it works than to follow this [step-by-step instructable](http://www.instructables.com/id/5-minute-Photo-Booth/)?

#### One-minute Set Up

* Pick a photo strip template with optional event name, date and logo
* Link Facebook and Dropbox accounts for auto sharing
* Link Google Cloud Print for printing
* Keep the app in foreground with passcode-protected Kiosk mode

#### Guest Experience

* Take pictures as in a photo booth, with count down timer and animated review panel
* Trigger with on-screen button or Bluetooth remote
* Find your photo strip on Facebook, Dropbox, or printed via Google Cloud Print
* Automatically return to the Capture screen for the next guest

### Build

To compile the applications you must have the [Android SDK](http://developer.android.com/sdk/index.html) set up. With your device connected, build and install **Flying PhotoBooth** with:

```
./gradlew :flying-photo-booth:installDebug
```
Some Wings Sharing endpoints may not work on your custom build as API keys from the service providers may be pinned to the release signing keys. You should find **donottranslate.xml** in each application and replace all API keys.

If you plan on distributing a fork of these applications, you must replace the following:

* Package names **com.groundupworks.flyingphotobooth** and **com.groundupworks.partyphotobooth**
* Application names **Flying PhotoBooth** and **Party PhotoBooth**
* Application icons and all branded graphics
* API keys

### License

Copyright (c) 2016 EXTP Production

Source code licensed under the [GPLv3](http://www.gnu.org/licenses/gpl-3.0.html)