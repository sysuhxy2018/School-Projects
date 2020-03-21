/**
 * Copyright 2017, Google, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

// [START gae_node_request_example]
const express = require('express');
var request = require('request');

const app = express();
app.use(express.static('dist/forecast'));

// Only for local debug
// app.use(function(req, res, next) {
//   res.header("Access-Control-Allow-Origin", "*"); // update to match the domain you will make the request from
//   res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
//   next();
// });

var location;
var googlePath = 'https://maps.googleapis.com/maps/api/geocode/json?address=';
var googlePos = null;
var lat = "", lng = "";

function getGoogleObj(loc, res) {
  var url = googlePath + loc.street + ',' + loc.city + ',' + loc.state + '&key=AIzaSyAwr8GaCxXJ70WJtLwTK1YSvnlew1-TbSs';
  url = encodeURI(url);
  var e = request(url, function(error,response,body){
        if(!error && response.statusCode==200){
          googlePos = JSON.parse(body);
          // If google api failed to fetch the coordinate, then return in advance.
          if (googlePos.status == "ZERO_RESULTS") {
            res.json(googlePos);
            return;
          }
          lat = googlePos.results[0].geometry.location.lat;
          lng = googlePos.results[0].geometry.location.lng;
          getWeatherObj(lat, lng, res);
        }
        else {
          console.log(error);
        }
    });
}

var weatherPath = 'https://api.darksky.net/forecast/e28ea2e81a05a24ce3e5d73c22c0399e/';
var weatherInfo = null;
function getWeatherObj(lat, lng, res) {
  var url = weatherPath + lat + ',' + lng;
  url = encodeURI(url);
  var e = request(url, function(error,response,body){
        if(!error && response.statusCode==200){
          weatherInfo = JSON.parse(body);
          res.json(weatherInfo);
        }
        else {
          console.log(error);
        }
    });
}

function getDetailObj(lat, lng, time, res) {
  var url = weatherPath + lat + ',' + lng + ',' + time;
  url = encodeURI(url);
  var e = request(url, function(error,response,body){
    if(!error && response.statusCode==200){
      weatherInfo = JSON.parse(body);
      res.json(weatherInfo);
    }
    else {
      console.log(error);
    }
  });
}

app.get('/weatherSearch', (req, res, next) => {
  location = req.query;
  if (location.time) {
    getDetailObj(location.lat, location.lon, location.time, res);
  }
  else {
    if (location.flag == "true") {
      getGoogleObj(location, res);
    }
    else {
      lat = location.lat;
      lng = location.lon;
      getWeatherObj(lat, lng, res);
    }
  }
})

var customPath = "https://www.googleapis.com/customsearch/v1?";
app.get('/imageSearch', (req, res, next) => {
   var body = req.query;
   var url = customPath+'q='+body.q+
   '&cx=013849470689090416768:hixbvoykocs&imgSize=huge&imgType=photo&num=1&searchType=image&key=AIzaSyAoJ7sQBpmIRKdCf2Ch7Ri9Wu6UMAk_RnI';
   url = encodeURI(url);
   var e = request(url, function(error,response,body){
    if(!error && response.statusCode==200){
      res.json(JSON.parse(body));
    }
    else {
      console.log(error);
    }
  });
})

var autoPath = "https://maps.googleapis.com/maps/api/place/autocomplete/json?";
app.get('/autoSearch', (req, res, next) => {
  var body = req.query;
  var url = autoPath + 'input=' + body.input +'&types=(cities)&language=en&key=AIzaSyAoJ7sQBpmIRKdCf2Ch7Ri9Wu6UMAk_RnI';
  url = encodeURI(url);
  var e = request(url, function(error,response,body){
    if(!error && response.statusCode==200){
      res.json(JSON.parse(body));
    }
    else {
      console.log(error);
    }
  });
})

// Start the server
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`App listening on port ${PORT}`);
  console.log('Press Ctrl+C to quit.');
});
// [END gae_node_request_example]

module.exports = app;
