
// Include the vis.js library
// <script src="https://unpkg.com/vis-network/standalone/umd/vis-network.min.js"></script>

// Create an HTML element to hold the chart
// <div id="mynetwork"></div>

// Documentation
// https://visjs.org/
// https://visjs.github.io/vis-network/docs/network/


/*
var nodes = new vis.DataSet([
    {id: 1, label: "Italy"},
    {id: 2, label: "France"},
    {id: 3, label: "Germany"},
    {id: 4, label: "Sweeden"},
    {id: 5, label: "Denmark"},
    {id: 6, label: "Ireland"},
    {id: 7, label: "Iceland"},
    {id: 8, label: "Holland"}
]);

var edges = new vis.DataSet([
    {from: 0, to: 1},
    {from: 0, to: 2},
    {from: 0, to: 3},
    {from: 0, to: 4},
    {from: 1, to: 5},
    {from: 2, to: 5},
    {from: 2, to: 5},
    {from: 3, to: 4},
    {from: 3, to: 5},
    {from: 4, to: 6},
    {from: 5, to: 7}
]);*/


let nodes = new vis.DataSet([]);
let edges = new vis.DataSet([]);

fetch('/network/nodes')
    .then(response => response.json())
    .then(data => {
        nodes.add(data);
        console.log(data);
    });

fetch('/network/edges')
    .then(response => response.json())
    .then(data => {
        edges.add(data);
        console.log(data);
    });




var container = document.getElementById('mynetwork');
var data = {
    nodes: nodes,
    edges: edges
};
var options = {
    autoResize: true,
    height: '100%',
    width: '100%',
    locale: 'fr',
    //locales: locales,
    clickToUse: false,
    //configure: {...},    // defined in the configure module.
    //edges: {...},        // defined in the edges module.
    //nodes: {...},        // defined in the nodes module.
    //groups: {...},       // defined in the groups module.
    //layout: {...},       // defined in the layout module.
    //interaction: {...},  // defined in the interaction module.
    //manipulation: {...}, // defined in the manipulation module.
    //physics: {...},
};
var network = new vis.Network(container, data, options);

