/* 
 * Interface JavaScript Functionality
 *
 */

// global variables
var objectList = {
    '0': "#wallControls",
    '1': "#doorControls"
};

var totaltime = 20;
var numpeople = 100;
var evactime = 10;
var threads = 4;
var astar = 0;
var astarfreq = 5;

function predict(numpeople, totaltime, threads, astar, astarfreq) {


    if (numpeople <= 516.5 &
        totaltime <= 81.5 &
        threads > 6 &
        threads <= 12) {

        return (
            3.5646 * totaltime
                + 0.7779 * numpeople
                + 20.1398 * astar
                - 0.7601 * astarfreq
                + 15.0699 * threads
                + 587.0387)
    }

    if (numpeople > 514 &
        totaltime > 90 &
        threads > 6 &
        astar > 0.5) {

        return (
            19.327 * totaltime
                + 2.646 * numpeople
                + 204.3934 * astar
                - 14.5561 * astarfreq
                + 78.9856 * threads
                - 2095.5259 )
    }

    if (threads > 2.5 &
        totaltime <= 112.5 &
        threads <= 12 &
        numpeople > 396.5 &
        totaltime > 61) {

        return (
            9.7549 * totaltime
                + 1.1797 * numpeople
                + 308.7049 * astar
                - 3.82 * astarfreq
                + 61.925 * threads
                - 486.6747 )
    }

    if (threads > 2.5 &
        totaltime > 66 &
        numpeople > 394.5 &
        astar <= 0.5 &
        threads <= 12) {

        return (
            7.5489 * totaltime
                + 1.881 * numpeople
                + 90.1609 * astar
                - 0.0162 * astarfreq
                + 82.9783 * threads
                - 1048.4026 )
    }

    if (numpeople > 514 &
        totaltime > 77 &
        threads > 2.5) {

        return (
            13.7569 * totaltime
                + 2.7741 * numpeople
                + 23.5601 * astar
                - 9.494 * astarfreq
                - 14.0216 * threads
                - 607.3578 )
    }

    if (threads > 2.5 &
        threads <= 12 &
        totaltime > 112.5 &
        numpeople <= 251) {

        return (
            2.9438 * totaltime
                + 2.1274 * numpeople
                + 228.6618 * astar
                - 0.2087 * astarfreq
                + 84.9401 * threads
                - 283.9304 )
    }

    if (threads > 2.5 &
        threads <= 12 &
        totaltime <= 112.5 &
        threads > 6) {

        return (
            6.4828 * totaltime
                + 0.9563 * numpeople
                + 116.3705 * astar
                - 0.1477 * astarfreq
                + 14.8232 * threads
                + 229.1136)
    }

    if (threads > 2.5 &
        threads > 6 &
        totaltime <= 62 &
        numpeople > 249.5) {

        return (
            8.4593 * totaltime
                + 0.5359 * numpeople
                + 40.0249 * astar
                - 1.2666 * astarfreq
                + 21.9137 * threads
                + 963.4053)
    }

    if (numpeople <= 514 &
        threads <= 6 &
        totaltime <= 112.5 &
        numpeople > 162.5 &
        totaltime <= 68.5) {

        return (
            6.8178 * totaltime
                + 1.3287 * numpeople
                + 50.0178 * astar
                + 0.0085 * astarfreq
                - 8.9875 * threads
                + 70.3466)
    }

    if (threads <= 2.5 &
        numpeople > 342 &
        totaltime > 92 &
        numpeople <= 721.5) {

        return (
            27.0586 * totaltime
                + 7.9471 * numpeople
                + 1360.8731 * astar
                - 11.6574 * astarfreq
                + 1.7764 * threads
                - 5018.1157 )
    }

    if (numpeople <= 725.5 &
        threads <= 6 &
        numpeople > 197 &
        totaltime > 53.5) {

        return (
            7.8351 * totaltime
                + 2.4993 * numpeople
                + 360.8229 * astar
                - 3.3332 * astarfreq
                - 134.9575 * threads
                - 128.9859 )
    }

    if (numpeople <= 726.5 &
        threads <= 6 &
        numpeople > 126 &
        numpeople <= 554.5) {

        return (
            5.1186 * totaltime
                + 0.9588 * numpeople
                + 26.786 * astar
                + 11.3543 * threads
                + 138.3602)
    }

    if (numpeople <= 726.5 &
        threads > 6 &
        threads <= 12 &
        astar > 0.5) {

        return (
            9.1215 * totaltime
                + 3.1433 * numpeople
                + 153.5898 * astar
                - 4.6113 * astarfreq
                + 31.901 * threads
                - 851.229)
    }

    if (totaltime <= 77.5 &
        threads <= 10 &
        numpeople > 341.5 &
        threads > 2.5) {

        return (
            11.6386 * totaltime
                + 0.9837 * numpeople
                + 52.5765 * astar
                - 31.4837 * threads
                + 115.693)
    }

    if (numpeople <= 726 &
        threads > 12 &
        totaltime > 61.5 &
        totaltime > 87.5 &
        astar > 0.5) {

        return (
            6.8145 * totaltime
                + 2.1283 * numpeople
                + 142.3808 * astar
                + 9.7776 * threads
                + 962.4972)
    }

    if (numpeople <= 726 &
        threads <= 6 &
        threads > 2.5) {

        return (
            2.497 * totaltime
                + 0.7797 * numpeople
                + 78.1246 * astar
                + 27.2359 * threads
                + 193.394)
    }

    if (numpeople <= 726 &
        threads > 12 &
        totaltime > 61.5 &
        totaltime > 102) {

        return (
            4.3501 * totaltime
                + 0.8054 * numpeople
                + 101.3056 * astar
                + 15.8115 * threads
                + 1459.601)
    }

    if (numpeople > 726 &
        totaltime > 89.5 &
        astar > 0.5) {

        return (
            50.8943 * totaltime
                + 11.7383 * numpeople
                + 1225.7413 * astar
                + 7.221 * threads
                - 10516.8589 )
    }

    if (numpeople <= 726 &
        threads > 4.5 &
        totaltime <= 61.5) {

        return (
            0.5014 * totaltime
                + 0.9088 * numpeople
                + 18.1923 * threads
                + 1185.9354)
    }

    if (numpeople <= 726 &
        threads <= 12 &
        numpeople > 198 &
        totaltime > 24.5) {

        return (
            5.0859 * totaltime
                + 1.5921 * numpeople
                + 0.5116 * astarfreq
                + 16.9189 * threads
                + 74.9076)
    }

    if (numpeople > 131 &
        totaltime <= 102) {

        return (
            29.8855 * totaltime
                + 1.419 * numpeople
                + 265.4152 * astar
                + 5.1587 * astarfreq
                - 19.2631 * threads
                - 619.2479)
    }

    if (numpeople <= 425.5 &
        totaltime <= 75.5) {

        return (
            2.3907 * totaltime
                + 2.5879 * numpeople
                - 3.8358 * astarfreq
                + 175.4042)
    }

    if (numpeople > 421) {

        return (
            20.4445 * totaltime
                + 7.1677 * numpeople
                - 8.1938 * astarfreq
                - 3530.563)
    }

    if (numpeople > 18.5) {

        return (
            2.5367 * totaltime
                + 3.3183 * numpeople
                + 128.3443 )
    }

    if (totaltime > 110) {

        return (
            -15.5058 * totaltime
                + 2619.576)
    }

    return ( 1627

        )
}

function resizeCanvas() {

    var canvas = document.getElementById("mainCanvas"); // canvas handle

    canvas.width = 1000;
    canvas.height = 1000;
}

function modeSelected(mode) {

    var curDiv = objectList[mode];
    var curPar = $(curDiv).parent();

    $(curDiv).siblings().hide();
    $(curDiv).show();

    // minimum div height of 30, the size of one row of small buttons
    if ($(curPar).height() < 30)
        $(curPar).height(30);
}

// update displayed text in an element
function updateVal(newVal, valName) {

    document.getElementById(valName).innerHTML = newVal;

    totaltime = parseInt(document.getElementById("totalTimeVal").innerHTML);

    evactime = parseInt(document.getElementById("evacTimeVal").innerHTML);

    numpeople = parseInt(document.getElementById("numPeopleVal").innerHTML);

    astarfreq = parseInt(document.getElementById("astarFreqVal").innerHTML);

    astar = parseInt(document.getElementById("astaron").value);

    threads = parseInt(document.getElementById("numThreads").value);

    if (astar == 0) {
        astarfreq = 100;
    }

    console.log(String(numpeople) + ", " + String(totaltime) + ", " + String(threads) + ", " + String(astar) + ", " + String(astarfreq));
    console.log(predict(numpeople, totaltime, threads, astar, astarfreq));
    document.getElementById("prediction").innerHTML = String(Math.round(predict(numpeople, totaltime, threads, astar, astarfreq) * 10)) + " ms";

}

function togglePanel() {

    var panelDiv = document.getElementById('panel');
    var toggleDiv = document.getElementById('panelToggle');
    var mainDiv = document.getElementById('main');

    if (panelDiv.style.visibility == 'hidden') {
        panelDiv.style.visibility = 'visible';
        toggleDiv.innerHTML = '<div id="toggleinner">&gt;&gt;</div>';
        toggleDiv.style.right = '250px';
        mainDiv.style.right = '280px';
    }
    else {
        panelDiv.style.visibility = 'hidden';
        toggleDiv.innerHTML = '<div id="toggleinner">&lt;&lt;</div>';
        toggleDiv.style.right = '0';
        mainDiv.style.right = '30px';
    }
}

function toggleOption(toggleId, elemId) {

    var elemDiv = document.getElementById(elemId);
    var toggleDiv = document.getElementById(toggleId);
    var innerh = toggleDiv.innerHTML;

    if (elemDiv.style.visibility == 'collapse') {
        elemDiv.style.visibility = 'inherit';
        toggleDiv.innerHTML = innerh;
    }
    else {
        elemDiv.style.visibility = 'collapse';
        toggleDiv.innerHTML = innerh;
    }
}

// triggered when window is loaded; initial setup of canvas, list population etc.
$(window).ready(function () {
    resizeCanvas();
});
