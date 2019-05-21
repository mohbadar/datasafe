package de.adorsys.datasafe.business.api.profile.dfs;

import de.adorsys.datasafe.business.api.types.UserID;
import de.adorsys.datasafe.business.api.types.UserIDAuth;
import de.adorsys.datasafe.business.api.types.resource.AbsoluteLocation;
import de.adorsys.datasafe.business.api.types.resource.PrivateResource;
import de.adorsys.datasafe.business.api.types.resource.PublicResource;
import de.adorsys.datasafe.business.api.types.resource.ResourceLocation;

public interface BucketAccessService {

    AbsoluteLocation<PrivateResource> privateAccessFor(UserIDAuth user, ResourceLocation bucket);
    AbsoluteLocation<PublicResource> publicAccessFor(UserID user, ResourceLocation bucket);
}
