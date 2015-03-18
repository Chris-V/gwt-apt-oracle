package org.chrisv.gwtaptoracle.sample.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.chrisv.gwtaptoracle.sample.model.Bee;
import org.chrisv.gwtaptoracle.sample.resources.Paths.BeesPaths;
import org.chrisv.gwtaptoracle.sample.resources.Paths.BeesPaths.BeePaths;
import org.chrisv.gwtaptoracle.sample.resources.Paths.BeesPaths.BeePaths.BeeParams;

@Path(BeesPaths.BEES)
public interface SomeResource {
    @GET
    @Path(BeePaths.BEE)
    Bee getVersion(@PathParam(BeeParams.BEE_ID) int id);

    @GET
    List<Bee> getAll();
}
