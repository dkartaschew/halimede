# Integration

Halimede is capable of direct integration with other Eclipse e4 RCP based
applications.

## Requirements 

To integrate, an additional plugin that provides a model fragment is required
to be developed by the integrator to provide the required model extensions to
the application model.

The general recommended method is to create command/handler/menu elements that
add the primary part (`CertificateManagerView`) to the application's primary 
part stack. (See the Eclipse IDE plugin bundle for an example).

The `CertificateManagerView` also requires the ID of the part stack to be 
supplied within the persitated state of the new defined part to ensure
new parts are added to the correct part stack. (See `ShowCertifcateManager` 
command in the Eclipse IDE plugin bundle for an example).

Otherwise Halimede is completely self-contained.

**Notes**

* Halimede utilises Bouncy Castle within the core plugin, however this is
**not** exported. If you wish to integrate with the core Certificate 
Authority classes directly, you will need to import Bouncy
Castle Bundles directly. Additionally, Halimede uses the Bouncy Castle Bundles
as downloaded directly from the central maven repository, and **not** those 
made available via Eclipse Orbit. (These have different Bundle IDs).
* Additional parts that Halimede add to the part stack are not persisted
to the application's model on shutdown. This is due to these additional parts
potentially containing passwords and/or decrypted materials.

