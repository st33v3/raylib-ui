import type {} from "./model.ts";

//read json from stdin
var input = "";
process.stdin.resume();
process.stdin.setEncoding('utf8');
process.stdin.on('data', function(chunk) {
  input += chunk;
});
process.stdin.on('eof', () => {
  var json = JSON.parse(input);
   convert(json);
   });


function convert(json) {
    var svg = '<svg xmlns="http://www.w3.org/2000/svg" width="' + json.width + '" height="' + json.height + '">';
    for (var i = 0; i < json.shapes.length; i++) {
        var shape = json.shapes[i];
        if (shape.type === 'circle') {
        svg += '<circle cx="' + shape.cx + '" cy="' + shape.cy + '" r="' + shape.r + '" fill="' + shape.fill + '" />';
        } else if (shape.type === 'rect') {
        svg += '<rect x="' + shape.x + '" y="' + shape.y + '" width="' + shape.width + '" height="' + shape.height + '" fill="' + shape.fill + '" />';
        }
    }
    svg += '</svg>';
    console.log(svg);
    }