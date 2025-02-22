import type { IBatch } from "./model.ts";
import {JSDOM} from "jsdom";

console.log("START");
//read json from stdin
var input = "";
process.stdin.resume();
process.stdin.setEncoding('utf8');
process.stdin.on('data', function(chunk) {
  input += chunk;
});
process.stdin.on('end', () => {
  var json = JSON.parse(input);
  console.log("JSON");
   convert(json);
   });


function convert(json: IBatch) {
    console.log("CONVERT");
    console.log(json);
    const dom = new JSDOM("<!DOCTYPE html><html><body><svg xmlns='http://www.w3.org/2000/svg'></svg></body></html>");
    const svg = dom.window.document.querySelector("svg");
    const doc = dom.window.document;
    const svgns = "http://www.w3.org/2000/svg" as const;
    var circle = doc.createElementNS(svgns, 'circle');
    circle.setAttribute('cx',"100");
    circle.setAttribute('cy',"200");
    circle.setAttribute('r',"50");
    circle.setAttribute('fill','red');
    circle.setAttribute('stroke','black');
    circle.setAttribute('stroke-width','20px');
    circle.setAttribute('stroke-opacity','0.5');
    svg.appendChild(circle);
    console.log("SVG");
    console.log(dom.serialize());
    
    }