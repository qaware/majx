# majx - Matching JSON expressively
[![Build Status](https://travis-ci.org/qaware/majx.svg?branch=master)](https://travis-ci.org/qaware/majx) [![Coverage Status](https://coveralls.io/repos/github/qaware/majx/badge.svg?branch=master)](https://coveralls.io/github/qaware/majx?branch=master) [![License](http://img.shields.io/badge/license-MIT-green.svg?style=flat)]()

Majx is a small, focused test library that helps to verify that a JSON document fulfils your expectations regarding 
structure and values. 

You provide a JSON document, that you wish to test - let's call it the **actual JSON document**
along with another JSON document that describes the things you with to verify - let's call this one the **pattern JSON document**.

The main idea of this lib is that we try to let you express almost all of your expectations in a flexible, readable 
and concise way within the **pattern JSON document**. To achieve this, the **pattern JSON document** may contain magic 
values, that are treated in a certain way. 

## Writing the pattern

An example pattern would be the following JSON:

```
{
  "brand" : "BMW",    (1)
  "color" : "...",    (2)
  "engine" : {        (3)
    "cylinders" : 4,  (4)
    "..." : "..."     (5)
  },
  "features" : [      (6) 
    "air con",        (7)
    "..."             (8)
  ]
}                     (9)
```

An **actual JSON document** that you match with this pattern ...

1. must have a property ``$.brand`` of type string with value ``"BMW"``.
2. must have a property ``$.color`` of any type with any value.
3. must have a property ``$.engine`` of type object.
4. must have a property ``$.engine.cylinders`` of type number with value ``4``
5. the object at position ``$.engine`` may have zero or more other properties, which we don't care about. The wildcard-property (``"..." : "..."``) must be the last entry of an object.
6. must have a property ``$.features`` of type array.
7. must have a property ``$.features[0]`` of type string with value ``"air con"``.
8. the array at position ``$.features`` may have zero or more other properties, which we don't care about. The wildcard element (``"..."``) must be the last entry of any array.
9. Note: The root object (``$``) may not have additional properties (as opposed to ``$.engine``, see above) 
because it has no wildcard-property (``"..." : "..."``) at the end.

## Matching an actual JSON document

That means this **actual JSON document** will pass the validation:

```
{
  "brand" : "BMW",    
  "color" : "red",    
  "engine" : {        
    "cylinders" : 4
  },
  "features" : [       
    "air con",        
    "head up display",
    "brake assist"             
  ]
}                     
```

... but this **actual JSON document** will cause a validation error:

```
{
  "brand" : "VW",       (1)
  "engine" : {        
    "cylinders" : "4"   (2)  
  },
  "features" : [
    "head up display",
    "air con"           (3)
  ]
}                       (4)
```

Why? Because the latter **actual JSON document** ...

1. does not have a property ``$.brand`` of type string with value``"BMW"``. Instead it's value is ``"VW"``.
2. does not have a property ``$.engine.cylinders`` of type number with value ``4``. Instead it is of type 
string and has value ``"4"``.
3. does not have a property ``$.features[0]`` of type string with value ``"air con"``. 
Instead, it's value is ``"head up display"``.
4. does not have a property ``$.color`` with any value.

## Maintainer

Claudius Boettcher, <claudius.boettcher@qaware.de>.

## License

This software is provided under the MIT open source license, read the `LICENSE` file for details.
