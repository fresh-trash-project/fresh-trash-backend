const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/chat-ws'
});

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/chat/1/message', (greeting) => {
        console.log('subscribe: ' + JSON.stringify(greeting.body));
        showGreeting(JSON.parse(greeting.body).message);
        showGreeting(JSON.parse(greeting.body).sentMemberId);
        showGreeting(JSON.parse(greeting.body).sentMemberNickname);
        showGreeting(JSON.parse(greeting.body).createdAt);
    });
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    var name = $("#name").val();
    console.log("publish: " + name);
    stompClient.publish({
        destination: "/app/1/message/1",
        body: JSON.stringify({'message': name})
    });
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $("#connect").click(() => connect());
    $("#disconnect").click(() => disconnect());
    $("#send").click(() => sendName());
});
