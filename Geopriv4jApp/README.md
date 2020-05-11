# Geopriv4jApp
A sample android application that implements the geopriv4j open-source project.

## Design
* This application uses the Maps SDK for android to collect the GPS locations.
* Here we are using the simple MapsActivity to simulate privacy using the rounding algorithm implemented in the geopriv4j project


## Privacy Methodology:
* We snap each latitude and longitude to the nearest point on a square grid with spacing ∆ in meters.
* Micinski, Kristopher, Philip Phelps, and Jeffrey S. Foster. "An empirical study of location truncation on android." Weather 2 (2013): 21.

*Acknolwedgement:* This research has been supported in part by NSF grant CNS-1951430 and UNC Charlotte. Any opinions, findings, and conclusions or recommendations expressed in this material are those of the author(s) and do not necessarily reflect the views of the sponsors.
