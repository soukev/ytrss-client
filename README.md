# ytrss-client

Client shows and saves RSS feeds from youtube channels and allows opening videos in mpv media player.

## Why?
This approach doesn't require Google Account and doesn't use YouTube API. This means at least a little more private YouTube viewing. I made this project as practice while learning Clojure.

## How does it work?
Application reads from RSS feed and saves new posts to local database (SQLite). You can browse all videos in table and after pressing `s` key play selected video in mpv player. 

## Building from source
Project is build with Leiningen.
To run program use `lein run` or you can build uberjar with `lein uberjar`.

## Download
You can download jar file from [here](https://github.com/soukev/ytrss-client/releases/tag/Release).

Run jar file with:

``` sh
$ java -jar ytrss-client-0.1.0-SNAPSHOT-standalone.jar
```

## Usage
Make sure `mpv` media player and `youtube-dl` are installed and that `mpv` is available in `$PATH`.

Keybindings:
 - Next:                    Enter/Down
 - Previous:                Shift+Enter/Up
 - Play in mpv:             S
 - Update subscriptions:    U

Config files are located in `~/.ytrss-client`. All subscriptions are in `subs.conf` file. File `config.conf` contains color settings.

To add RSS feed to `subs.conf` you need to know channel ID. This ID can be acquired from channel address.

Channel url with ID:
```
https://www.youtube.com/channel/ID
```

RSS feed url:
```
https://www.youtube.com/feeds/videos.xml?channel_id=ID
```

Insert this RSS feed url into `subs.conf`, run app and update with `u` key.
