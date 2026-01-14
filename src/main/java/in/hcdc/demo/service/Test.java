/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package in.hcdc.demo.service;

import in.hcdc.demo.model.BiodataRequest;
import in.hcdc.demo.model.SampleBiodataFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Vaibhav
 */
@Service
public class Test {

    @Autowired
    private BiodataImageRendererService biodataImageRendererService;

    @PostConstruct
    public void generateSample() {
        BiodataRequest sample = SampleBiodataFactory.create();
        biodataImageRendererService.renderBiodata(sample, "bio1");
    }

}
