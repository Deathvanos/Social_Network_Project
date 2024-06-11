function addContentInput () {
    var contentInput = document.getElementById("content");
    contentInput.type = "text";

    var sendButton = document.createElement("button");
    sendButton.type = "submit";
    sendButton.className = "btn btn-outline-success start-discussion-button";
    sendButton.innerText = "Send"

    var form = document.getElementById("create-discussion-form");
    form.appendChild(sendButton);
}