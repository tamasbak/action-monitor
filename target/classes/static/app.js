var stompClient = null;

function setConnected(connected) {
	$("#conversation").hide();

	if (connected) {
    	$("#connect").show();
        $("#conversation").show();
    }

    $("#action").html("");
}

function connect() {
    var socket = new SockJS('/action-monitor-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/action-monitor', function (greeting) {
        	showAction(JSON.parse(greeting.body).content);
        });
    });
}

function showAction(message) {
    $("#action").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
	$("#connect").hide();
    connect();
});