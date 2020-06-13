# Geopriv4j: An Open Source Repository for Location Privacy
Geopriv4j implements location privacy methods in Java (JDK 14.0.1).  Methods in the repository do not rely on other trusted parties/users/servers, and protect the user's location on-the-fly.  Please see the below for a list of methods currently implemented in the repository.  

## Location Privacy Methods:

### Generalization-based:
* Rounding/trucation - This method has been implemented from the paper by Krumm, John. "Inference attacks on location tracks." International Conference on Pervasive Computing. Springer, Berlin, Heidelberg, 2007 [paper](https://www.microsoft.com/en-us/research/wp-content/uploads/2016/12/inference-attack-refined02-distribute.pdf) and  Micinski, Kristopher, Philip Phelps, and Jeffrey S. Foster. "An empirical study of location truncation on android." Weather 2 (2013): 21. [paper](http://www.cs.tufts.edu/~jfoster/papers/most13.pdf)
* Spatial cloaking - This method has been implemented from the paper by Krumm, John. "Inference attacks on location tracks." International Conference on Pervasive Computing. Springer, Berlin, Heidelberg, 2007. [paper](https://www.microsoft.com/en-us/research/wp-content/uploads/2016/12/inference-attack-refined02-distribute.pdf)

### Dummy-based:
* SpotME - This has been implemented based on the paper by D. Quercia, I. Leontiadis, L. McNamara, C. Mascolo and J. Crowcroft, "SpotME If You Can: Randomized Responses for Location Obfuscation on Mobile Phones," 2011 31st International Conference onDistributed Computing Systems, Minneapolis, MN, 2011, pp. 363-372. [paper](https://ieeexplore.ieee.org/document/5961717)
* Moving in the Neighborhood - This method is implemented based on the paper by H. Kido, Y. Yanagisawa and T. Satoh, "An anonymous communication technique using dummies for location-based services," ICPS '05. Proceedings. International Conference on Pervasive Services, 2005., Santorini, Greece, 2005, pp. 88-97. [paper](https://ieeexplore.ieee.org/abstract/document/1506394)

### Perturbation-based:
* Noise - This method has been implemented from the paper by Krumm, John. "Inference attacks on location tracks." International Conference on Pervasive Computing. Springer, Berlin, Heidelberg, 2007. [paper](https://www.microsoft.com/en-us/research/wp-content/uploads/2016/12/inference-attack-refined02-distribute.pdf)
* Various-sized Hilbert Curve - This has been implemented based on the paper by Pingley, Aniket, et al. "Cap: A context-aware privacy protection system for location-based services." 2009 29th IEEE International Conference on Distributed Computing Systems. IEEE, 2009. [paper](https://www2.seas.gwu.edu/~nzhang10/cap/cap/Welcome_files/paper.pdf)
* Laplace - This method is implemented based on the paper by Andrés, Miguel E., et al.  "Geo-indistinguishability: Differential privacy for location-based systems." Proceedings of the 2013 ACM SIGSAC conference on Computer & communications security. 2013. [paper](http://www.lix.polytechnique.fr/~catuscia/papers/Geolocation/geo.pdf)

## Usage Examples
A usage example is provided for each location privacy method.  Synthetic location traces can be found in "data".

## Test Android App
A sample app is provided to illustrate the adoption of location privacy methods.  [Video](https://drive.google.com/file/d/1IBTvD7EH-sFyUZmwia_x1HBBpUMpulZO/view?usp=sharing)

*Acknolwedgement:* This research has been supported in part by NSF grant CNS-1951430 and UNC Charlotte. Any opinions, findings, and conclusions or recommendations expressed in this material are those of the author(s) and do not necessarily reflect the views of the sponsors.
