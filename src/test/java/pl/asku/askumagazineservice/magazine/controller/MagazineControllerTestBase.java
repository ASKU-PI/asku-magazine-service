package pl.asku.askumagazineservice.magazine.controller;

import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.controller.MagazineController;
import pl.asku.askumagazineservice.helpers.data.AuthenticationProvider;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.magazine.service.MagazineService;
import pl.asku.askumagazineservice.magazine.service.MagazineServiceTestBase;

public class MagazineControllerTestBase extends MagazineServiceTestBase {
    protected final MagazineController magazineController;

    protected final AuthenticationProvider authenticationProvider;

    @Autowired
    public MagazineControllerTestBase(
            MagazineService magazineService,
            MagazineDataProvider magazineDataProvider,
            MagazineController magazineController, AuthenticationProvider authenticationProvider,
            ImageServiceClient imageServiceClient) {
        super(magazineService, magazineDataProvider, imageServiceClient);
        this.magazineController = magazineController;
        this.authenticationProvider = authenticationProvider;
    }
}
