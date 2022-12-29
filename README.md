# figures
spring boot project for managing figures
description of task:

Create Api to add geometric figures to database.
We support the following figures: Square, Circle, Rectangle, but in the future we can do more.

Request for adding figures should look like this:
@POST /api/v1/shapes 
body: { type: "NAME", parameters: [p1, p2 p3] }
e.g. for square: { "type": "SQUARE", parameters: [10.0]}
should create a square with a side of 10

response: (for this example)
201 created, body: { "id", "type", "width", "version", "createdBy", "createdAt", "lastModifiedAt", "lastModifiedBy", "area", "perimeter" }

endpoint is to be available only to logged-in users with the CREATOR role

second endpoint:
@GET /api/v1/shapes?parameters....
where parameters is for example:
?createdBy=...&type=...&areaFrom=...&areaTo...&perimeterFrom=...&perimeterTo=...&widthFrom=...&widthTo=...&radiusFrom=...&radiusTo=....
generally can search by:
- figure type
- area from, area to
- circumference from, circumference to
- creation date from, to
- who created,
- after figure parameters, from - to
 
key requirement: if we want to add support for a new figure, we can not modify any of the existing classes, only the creation of new classes is involved.
