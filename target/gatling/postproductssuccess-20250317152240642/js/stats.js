var stats = {
    type: "GROUP",
name: "All Requests",
path: "",
pathFormatted: "group_missing-name--1146707516",
stats: {
    "name": "All Requests",
    "numberOfRequests": {
        "total": "98",
        "ok": "98",
        "ko": "0"
    },
    "minResponseTime": {
        "total": "669",
        "ok": "669",
        "ko": "-"
    },
    "maxResponseTime": {
        "total": "871",
        "ok": "871",
        "ko": "-"
    },
    "meanResponseTime": {
        "total": "704",
        "ok": "704",
        "ko": "-"
    },
    "standardDeviation": {
        "total": "45",
        "ok": "45",
        "ko": "-"
    },
    "percentiles1": {
        "total": "689",
        "ok": "689",
        "ko": "-"
    },
    "percentiles2": {
        "total": "699",
        "ok": "699",
        "ko": "-"
    },
    "percentiles3": {
        "total": "849",
        "ok": "849",
        "ko": "-"
    },
    "percentiles4": {
        "total": "853",
        "ok": "853",
        "ko": "-"
    },
    "group1": {
    "name": "t < 800 ms",
    "htmlName": "t < 800 ms",
    "count": 91,
    "percentage": 92.85714285714286
},
    "group2": {
    "name": "800 ms <= t < 1200 ms",
    "htmlName": "t >= 800 ms <br> t < 1200 ms",
    "count": 7,
    "percentage": 7.142857142857142
},
    "group3": {
    "name": "t >= 1200 ms",
    "htmlName": "t >= 1200 ms",
    "count": 0,
    "percentage": 0.0
},
    "group4": {
    "name": "failed",
    "htmlName": "failed",
    "count": 0,
    "percentage": 0.0
},
    "meanNumberOfRequestsPerSecond": {
        "total": "8.91",
        "ok": "8.91",
        "ko": "-"
    }
},
contents: {
"req_post-model-152481705": {
        type: "REQUEST",
        name: "post model",
path: "post model",
pathFormatted: "req_post-model-152481705",
stats: {
    "name": "post model",
    "numberOfRequests": {
        "total": "98",
        "ok": "98",
        "ko": "0"
    },
    "minResponseTime": {
        "total": "669",
        "ok": "669",
        "ko": "-"
    },
    "maxResponseTime": {
        "total": "871",
        "ok": "871",
        "ko": "-"
    },
    "meanResponseTime": {
        "total": "704",
        "ok": "704",
        "ko": "-"
    },
    "standardDeviation": {
        "total": "45",
        "ok": "45",
        "ko": "-"
    },
    "percentiles1": {
        "total": "689",
        "ok": "689",
        "ko": "-"
    },
    "percentiles2": {
        "total": "699",
        "ok": "699",
        "ko": "-"
    },
    "percentiles3": {
        "total": "849",
        "ok": "849",
        "ko": "-"
    },
    "percentiles4": {
        "total": "853",
        "ok": "853",
        "ko": "-"
    },
    "group1": {
    "name": "t < 800 ms",
    "htmlName": "t < 800 ms",
    "count": 91,
    "percentage": 92.85714285714286
},
    "group2": {
    "name": "800 ms <= t < 1200 ms",
    "htmlName": "t >= 800 ms <br> t < 1200 ms",
    "count": 7,
    "percentage": 7.142857142857142
},
    "group3": {
    "name": "t >= 1200 ms",
    "htmlName": "t >= 1200 ms",
    "count": 0,
    "percentage": 0.0
},
    "group4": {
    "name": "failed",
    "htmlName": "failed",
    "count": 0,
    "percentage": 0.0
},
    "meanNumberOfRequestsPerSecond": {
        "total": "8.91",
        "ok": "8.91",
        "ko": "-"
    }
}
    }
}

}

function fillStats(stat){
    $("#numberOfRequests").append(stat.numberOfRequests.total);
    $("#numberOfRequestsOK").append(stat.numberOfRequests.ok);
    $("#numberOfRequestsKO").append(stat.numberOfRequests.ko);

    $("#minResponseTime").append(stat.minResponseTime.total);
    $("#minResponseTimeOK").append(stat.minResponseTime.ok);
    $("#minResponseTimeKO").append(stat.minResponseTime.ko);

    $("#maxResponseTime").append(stat.maxResponseTime.total);
    $("#maxResponseTimeOK").append(stat.maxResponseTime.ok);
    $("#maxResponseTimeKO").append(stat.maxResponseTime.ko);

    $("#meanResponseTime").append(stat.meanResponseTime.total);
    $("#meanResponseTimeOK").append(stat.meanResponseTime.ok);
    $("#meanResponseTimeKO").append(stat.meanResponseTime.ko);

    $("#standardDeviation").append(stat.standardDeviation.total);
    $("#standardDeviationOK").append(stat.standardDeviation.ok);
    $("#standardDeviationKO").append(stat.standardDeviation.ko);

    $("#percentiles1").append(stat.percentiles1.total);
    $("#percentiles1OK").append(stat.percentiles1.ok);
    $("#percentiles1KO").append(stat.percentiles1.ko);

    $("#percentiles2").append(stat.percentiles2.total);
    $("#percentiles2OK").append(stat.percentiles2.ok);
    $("#percentiles2KO").append(stat.percentiles2.ko);

    $("#percentiles3").append(stat.percentiles3.total);
    $("#percentiles3OK").append(stat.percentiles3.ok);
    $("#percentiles3KO").append(stat.percentiles3.ko);

    $("#percentiles4").append(stat.percentiles4.total);
    $("#percentiles4OK").append(stat.percentiles4.ok);
    $("#percentiles4KO").append(stat.percentiles4.ko);

    $("#meanNumberOfRequestsPerSecond").append(stat.meanNumberOfRequestsPerSecond.total);
    $("#meanNumberOfRequestsPerSecondOK").append(stat.meanNumberOfRequestsPerSecond.ok);
    $("#meanNumberOfRequestsPerSecondKO").append(stat.meanNumberOfRequestsPerSecond.ko);
}
