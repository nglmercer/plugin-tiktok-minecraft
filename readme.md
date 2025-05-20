# Introduction
A Java library inspired by [TikTokLive](https://github.com/isaackogan/TikTokLive) and [TikTokLiveSharp](https://github.com/frankvHoof93/TikTokLiveSharp). Use it to receive live stream events such as comments and gifts in realtime from [TikTok LIVE](https://www.tiktok.com/live) by connecting to TikTok's internal WebCast push service.
The library includes a wrapper that connects to the WebCast service using just the username (`uniqueId`). This allows you to connect to your own live chat as well as the live chat of other streamers.
No credentials are required. Events such as [Members Joining](#member), [Gifts](#gift), [Subscriptions](#subscribe), [Viewers](#roomuser), [Follows](#social), [Shares](#social), [Questions](#questionnew), [Likes](#like) and [Battles](#linkmicbattle) can be tracked.


**NOTE:** This is not an official API. It's a reverse engineering project.

#### Overview
- [Getting started](#getting-started)
- [Events](#events)
- [Extensions](#extensions)
- [Listeners](#listeners)
- [Contributing](#contributing)

## Getting started

1. Install the package

Maven
```xml
   <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

   <dependencies>
         <dependency>
            <groupId>com.github.jwdeveloper.TikTok-Live-Java</groupId>
            <artifactId>Client</artifactId>
             <version>1.10.0-Release</version>
            <scope>compile</scope>
        </dependency>
   </dependencies>
```

Gradle
```gradle
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}

dependencies {
	        implementation 'com.github.jwdeveloper.TikTok-Live-Java:Client:1.10.0-Release'
	}
```

2. Create your first chat connection

```java

TikTokLive.newClient("bangbetmenygy")
        .onGift((liveClient, event) ->
        {
            String message = switch (event.getGift()) {
                case ROSE -> "ROSE!";
                case GG -> "GOOD GAME";
                case TIKTOK -> "Ye";
                case CORGI -> "Nice gift";
                default -> "Thank you for " + event.getGift().getName();
            };
            System.out.println(event.getUser().getProfileName() + " sends " + message);
        })
        .onGiftCombo((liveClient, event) ->
        {
            System.out.println(event.getComboState()+ " " + event.getCombo() + " " + event.getGift().getName());
        })
        .onRoomInfo((liveClient, event) ->
        {
            var roomInfo = event.getRoomInfo();
            System.out.println("Room Id: "+roomInfo.getRoomId());
            System.out.println("Likes: "+roomInfo.getLikesCount());
            System.out.println("Viewers: "+roomInfo.getViewersCount());
        })
        .onJoin((liveClient, event) ->
        {
            System.out.println(event.getUser().getProfileName() + "Hello on my stream! ");
        })
        .onConnected((liveClient, event) ->
        {
            System.out.println("Connected to live ");
        })
        .onError((liveClient, event) ->
        {
            System.out.println("Error! " + event.getException().getMessage());
        })
        .buildAndConnect();

```
3. Configure (optional)

```java

        TikTokLive.newClient("bangbetmenygy")
                .configure((settings) ->
                {
                    settings.setHostName("bangbetmenygy"); // This method is useful in case you want change hostname later
                    settings.setClientLanguage("en"); // Language
                    settings.setTimeout(Duration.ofSeconds(2)); // Connection timeout
                    settings.setLogLevel(Level.ALL); // Log level
                    settings.setPrintToConsole(true); // Printing all logs to console even if log level is Level.OFF
                    settings.setRetryOnConnectionFailure(true); // Reconnecting if TikTok user is offline
                    settings.setRetryConnectionTimeout(Duration.ofSeconds(1)); // Timeout before next reconnection

                    //Optional: Sometimes not every message from chat are send to TikTokLiveJava to fix this issue you can set sessionId
                    // documentation how to obtain sessionId https://github.com/isaackogan/TikTok-Live-Connector#send-chat-messages
                    settings.setSessionId("86c3c8bf4b17ebb2d74bb7fa66fd0000");

                    //Optional:
                    //RoomId can be used as an override if you're having issues with HostId.
                    //You can find it in the HTML for the livestream-page
                    settings.setRoomId("XXXXXXXXXXXXXXXXX");
                })
                .buildAndConnect();
        //  
```


## Events



## Events



**Control**:

- [onReconnecting](#onreconnecting-tiktokreconnectingevent)
- [onError](#onerror-tiktokerrorevent)
- [onConnected](#onconnected-tiktokconnectedevent)
- [onDisconnected](#ondisconnected-tiktokdisconnectedevent)

**Message**:

- [onEvent](#onevent-tiktokevent)
- [onEvent](#onevent-tiktokevent)
- [onComment](#oncomment-tiktokcommentevent)
- [onRoomInfo](#onroominfo-tiktokroominfoevent)
- [onGift](#ongift-tiktokgiftevent)
- [onSubscribe](#onsubscribe-tiktoksubscribeevent)
- [onFollow](#onfollow-tiktokfollowevent)
- [onGiftCombo](#ongiftcombo-tiktokgiftcomboevent)
- [onLiveEnded](#onliveended-tiktokliveendedevent)
- [onQuestion](#onquestion-tiktokquestionevent)
- [onShare](#onshare-tiktokshareevent)
- [onLiveUnpaused](#onliveunpaused-tiktokliveunpausedevent)
- [onEmote](#onemote-tiktokemoteevent)
- [onJoin](#onjoin-tiktokjoinevent)
- [onLike](#onlike-tiktoklikeevent)
- [onLivePaused](#onlivepaused-tiktoklivepausedevent)

**Debug**:

- [onWebsocketResponse](#onwebsocketresponse-tiktokwebsocketresponseevent)
- [onWebsocketUnhandledMessage](#onwebsocketunhandledmessage-tiktokwebsocketunhandledmessageevent)
- [onHttpResponse](#onhttpresponse-tiktokhttpresponseevent)
- [onWebsocketMessage](#onwebsocketmessage-tiktokwebsocketmessageevent)
# Examples
<br>

## onReconnecting [TikTokReconnectingEvent](https://github.com/jwdeveloper/TikTokLiveJava/blob/master/API/src/main/java/io/github/jwdeveloper/tiktok/data/events/TikTokReconnectingEvent.java)



```java
TikTokLive.newClient("host-name")
.onReconnecting((liveClient, event) ->
{

})
.buildAndConnect();
```



<br>

## onError [TikTokErrorEvent](https://github.com/jwdeveloper/TikTokLiveJava/blob/master/API/src/main/java/io/github/jwdeveloper/tiktok/data/events/TikTokErrorEvent.java)


General error event. You should handle this.


```java
TikTokLive.newClient("host-name")
.onError((liveClient, event) ->
{

})
.buildAndConnect();
```



<br>

## onConnected [TikTokConnectedEvent](https://github.com/jwdeveloper/TikTokLiveJava/blob/master/API/src/main/java/io/github/jwdeveloper/tiktok/data/events/TikTokConnectedEvent.java)


Triggered when the connection is successfully established.


```java
TikTokLive.newClient("host-name")
.onConnected((liveClient, event) ->
{

})
.buildAndConnect();
```



<br>

## onDisconnected [TikTokDisconnectedEvent](https://github.com/jwdeveloper/TikTokLiveJava/blob/master/API/src/main/java/io/github/jwdeveloper/tiktok/data/events/TikTokDisconnectedEvent.java)


Triggered when the connection gets disconnected. In that case you can call connect() again to have a reconnect logic.
Note that you should wait a little bit before attempting a reconnect to to avoid being rate-limited.


```java
TikTokLive.newClient("host-name")
.onDisconnected((liveClient, event) ->
{

})
.buildAndConnect();
```



<br>

## onEvent [TikTokEvent](https://github.com/jwdeveloper/TikTokLiveJava/blob/master/API/src/main/java/io/github/jwdeveloper/tiktok/data/events/common/TikTokEvent.java)


Base class for all events


```java
TikTokLive.newClient("host-name")
.onEvent((liveClient, event) ->
{

})
.buildAndConnect();
```



<br>

## onEvent [TikTokEvent](https://github.com/jwdeveloper/TikTokLiveJava/blob/master/API/src/main/java/io/github/jwdeveloper/tiktok/data/events/common/TikTokEvent.java)


Base class for all events


```java
TikTokLive.newClient("host-name")
.onEvent((liveClient, event) ->
{

})
.buildAndConnect();
```



<br>

## onComment [TikTokCommentEvent](https://github.com/jwdeveloper/TikTokLiveJava/blob/master/API/src/main/java/io/github/jwdeveloper/tiktok/data/events/TikTokCommentEvent.java)


Triggered every time a new chat comment arrives.


```java
TikTokLive.newClient("host-name")
.onComment((liveClient, event) ->
{

})
.buildAndConnect();
```



<br>

## onRoomInfo [TikTokRoomInfoEvent](https://github.com/jwdeveloper/TikTokLiveJava/blob/master/API/src/main/java/io/github/jwdeveloper/tiktok/data/events/room/TikTokRoomInfoEvent.java)


Triggered when LiveRoomInfo got updated such as likes, viewers, ranking ....


```java
TikTokLive.newClient("host-name")
.onRoomInfo((liveClient, event) ->
{

})
.buildAndConnect();
```



<br>

## onGift [TikTokGiftEvent](https://github.com/jwdeveloper/TikTokLiveJava/blob/master/API/src/main/java/io/github/jwdeveloper/tiktok/data/events/gift/TikTokGiftEvent.java)


Triggered when user sends gifts that has
no combo (most of expensive gifts)
or if combo has finished


```java
TikTokLive.newClient("host-name")
.onGift((liveClient, event) ->
{

})
.buildAndConnect();
```



<br>

## onSubscribe [TikTokSubscribeEvent](https://github.com/jwdeveloper/TikTokLiveJava/blob/master/API/src/main/java/io/github/jwdeveloper/tiktok/data/events/TikTokSubscribeEvent.java)


Triggers when a user creates a subscription.


```java
TikTokLive.newClient("host-name")
.onSubscribe((liveClient, event) ->
{

})
.buildAndConnect();
```



<br>

## onFollow [TikTokFollowEvent](https://github.com/jwdeveloper/TikTokLiveJava/blob/master/API/src/main/java/io/github/jwdeveloper/tiktok/data/events/social/TikTokFollowEvent.java)


Triggers when a user follows the streamer. Based on social event.


```java
TikTokLive.newClient("host-name")
.onFollow((liveClient, event) ->
{

})
.buildAndConnect();
```



<br>

## onGiftCombo [TikTokGiftComboEvent](https://github.com/jwdeveloper/TikTokLiveJava/blob/master/API/src/main/java/io/github/jwdeveloper/tiktok/data/events/gift/TikTokGiftComboEvent.java)


Triggered every time gift is sent

@see GiftSendType it has 3 states

  <p>Example when user sends gift with combo</p>
  <p>>Combo: 1  -> comboState = GiftSendType.Begin</p>
  <p>Combo: 4 -> comboState = GiftSendType.Active</p>
  <p>Combo: 8 -> comboState = GiftSendType.Active</p>
  <p>Combo: 12 -> comboState = GiftSendType.Finished</p>
  <p>
  Remember if comboState is Finished both TikTokGiftComboEvent and TikTokGiftEvent event gets triggered


```java
TikTokLive.newClient("host-name")
.onGiftCombo((liveClient, event) ->
{

})
.buildAndConnect();
```



<br>

## onLiveEnded [TikTokLiveEndedEvent](https://github.com/jwdeveloper/TikTokLiveJava/blob/master/API/src/main/java/io/github/jwdeveloper/tiktok/data/events/TikTokLiveEndedEvent.java)


Triggered when the live stream gets terminated by the host. Will also trigger the TikTokDisconnectedEvent event.


```java
TikTokLive.newClient("host-name")
.onLiveEnded((liveClient, event) ->
{

})
.buildAndConnect();
```



<br>

## onQuestion [TikTokQuestionEvent](https://github.com/jwdeveloper/TikTokLiveJava/blob/master/API/src/main/java/io/github/jwdeveloper/tiktok/data/events/TikTokQuestionEvent.java)


Triggered every time someone asks a new question via the question feature.


```java
TikTokLive.newClient("host-name")
.onQuestion((liveClient, event) ->
{

})
.buildAndConnect();
```



<br>

## onShare [TikTokShareEvent](https://github.com/jwdeveloper/TikTokLiveJava/blob/master/API/src/main/java/io/github/jwdeveloper/tiktok/data/events/social/TikTokShareEvent.java)


Triggers when a user shares the stream. Based on social event.


```java
TikTokLive.newClient("host-name")
.onShare((liveClient, event) ->
{

})
.buildAndConnect();
```



<br>

## onLiveUnpaused [TikTokLiveUnpausedEvent](https://github.com/jwdeveloper/TikTokLiveJava/blob/master/API/src/main/java/io/github/jwdeveloper/tiktok/data/events/TikTokLiveUnpausedEvent.java)



```java
TikTokLive.newClient("host-name")
.onLiveUnpaused((liveClient, event) ->
{

})
.buildAndConnect();
```



<br>

## onEmote [TikTokEmoteEvent](https://github.com/jwdeveloper/TikTokLiveJava/blob/master/API/src/main/java/io/github/jwdeveloper/tiktok/data/events/TikTokEmoteEvent.java)


Triggered every time a subscriber sends an emote (sticker).


```java
TikTokLive.newClient("host-name")
.onEmote((liveClient, event) ->
{

})
.buildAndConnect();
```



<br>

## onJoin [TikTokJoinEvent](https://github.com/jwdeveloper/TikTokLiveJava/blob/master/API/src/main/java/io/github/jwdeveloper/tiktok/data/events/social/TikTokJoinEvent.java)



```java
TikTokLive.newClient("host-name")
.onJoin((liveClient, event) ->
{

})
.buildAndConnect();
```



<br>

## onLike [TikTokLikeEvent](https://github.com/jwdeveloper/TikTokLiveJava/blob/master/API/src/main/java/io/github/jwdeveloper/tiktok/data/events/social/TikTokLikeEvent.java)


Triggered when a viewer sends likes to the streamer. For streams with many viewers, this event is not always triggered by TikTok.


```java
TikTokLive.newClient("host-name")
.onLike((liveClient, event) ->
{

})
.buildAndConnect();
```



<br>

## onLivePaused [TikTokLivePausedEvent](https://github.com/jwdeveloper/TikTokLiveJava/blob/master/API/src/main/java/io/github/jwdeveloper/tiktok/data/events/TikTokLivePausedEvent.java)



```java
TikTokLive.newClient("host-name")
.onLivePaused((liveClient, event) ->
{

})
.buildAndConnect();
```



<br>

## onWebsocketResponse [TikTokWebsocketResponseEvent](https://github.com/jwdeveloper/TikTokLiveJava/blob/master/API/src/main/java/io/github/jwdeveloper/tiktok/data/events/websocket/TikTokWebsocketResponseEvent.java)



```java
TikTokLive.newClient("host-name")
.onWebsocketResponse((liveClient, event) ->
{

})
.buildAndConnect();
```



<br>

## onWebsocketUnhandledMessage [TikTokWebsocketUnhandledMessageEvent](https://github.com/jwdeveloper/TikTokLiveJava/blob/master/API/src/main/java/io/github/jwdeveloper/tiktok/data/events/websocket/TikTokWebsocketUnhandledMessageEvent.java)


Triggered every time a protobuf encoded webcast message arrives. You can deserialize the binary object depending on the use case.


```java
TikTokLive.newClient("host-name")
.onWebsocketUnhandledMessage((liveClient, event) ->
{

})
.buildAndConnect();
```



<br>

## onHttpResponse [TikTokHttpResponseEvent](https://github.com/jwdeveloper/TikTokLiveJava/blob/master/API/src/main/java/io/github/jwdeveloper/tiktok/data/events/http/TikTokHttpResponseEvent.java)



```java
TikTokLive.newClient("host-name")
.onHttpResponse((liveClient, event) ->
{

})
.buildAndConnect();
```



<br>

## onWebsocketMessage [TikTokWebsocketMessageEvent](https://github.com/jwdeveloper/TikTokLiveJava/blob/master/API/src/main/java/io/github/jwdeveloper/tiktok/data/events/websocket/TikTokWebsocketMessageEvent.java)


Triggered every time TikTok sends data. Data incoming as protobuf message.
You can deserialize the binary object depending on the use case.


```java
TikTokLive.newClient("host-name")
.onWebsocketMessage((liveClient, event) ->
{

})
.buildAndConnect();
```






<br>


## Extensions

List of extensions (addons) to TiktokLiveJava
that will save your time

- [Video Recorder](https://github.com/jwdeveloper/TikTokLiveJava/tree/master/extension-recorder)
- [Live data collector to database](https://github.com/jwdeveloper/TikTokLiveJava/tree/master/extension-collector)



## Listeners

```java

/**
 *
 *  Listeners are an alternative way of handling events.
 *  I would to suggest to use then when logic of handing event
 *  is more complex
 *
 */
public static void main(String[] args) throws IOException {
        showLogo();
        CustomListener customListener = new CustomListener();

        TikTokLive.newClient(SimpleExample.TIKTOK_HOSTNAME)
        .addListener(customListener)
        .buildAndConnect();
        System.in.read();
        }

/**
 *
 *  Method in TikTokEventListener should meet 4 requirements to be detected
 *         - must have @TikTokEventHandler annotation
 *         - must have 2 parameters
 *         - first parameter must be LiveClient
 *         - second must be class that extending TikTokEvent
 */

public static class CustomListener {

    @TikTokEventObserver
    public void onLike(LiveClient liveClient, TikTokLikeEvent event) {
        System.out.println(event.toString());
    }

    @TikTokEventObserver
    public void onError(LiveClient liveClient, TikTokErrorEvent event) {
        //  event.getException().printStackTrace();
    }

    @TikTokEventObserver
    public void onComment(LiveClient liveClient, TikTokCommentEvent event) {
        var userName = event.getUser().getName();
        var text = event.getText();
        liveClient.getLogger().info(userName + ": " + text);
    }

    @TikTokEventObserver
    public void onGift(LiveClient liveClient, TikTokGiftEvent event) {
        var message = switch (event.getGift()) {
            case ROSE -> "Thanks :)";
            case APPETIZERS -> ":OO";
            case APRIL -> ":D";
            case TIKTOK -> ":P";
            case CAP -> ":F";
            default -> ":I";
        };
        liveClient.getLogger().info(message);
    }

    @TikTokEventHandler
    public void onAnyEvent(LiveClient liveClient, TikTokEvent event) {
        liveClient.getLogger().info(event.getClass().getSimpleName());
    }

}

```
