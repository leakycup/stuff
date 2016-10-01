function doWork(e) {
    var data = e.data;
    var id = data[0];
    var timeOut = data[1];
    var start = Date.now();
    var end = start + timeOut;
    while(Date.now() < end); //busy wait
    self.postMessage(id);
}

self.addEventListener('message', doWork, false);

