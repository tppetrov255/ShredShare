package com.shredshare.ShredShare.Controller;

import com.shredshare.ShredShare.Entity.Resort;
import com.shredshare.ShredShare.Service.ResortService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resorts")
public class ResortController {

    private final ResortService resortService;

    public ResortController(ResortService resortService) {
        this.resortService = resortService;
    }

    @GetMapping
    public List<Resort> getAllResorts() {
        return resortService.getAllResorts();
    }

    @GetMapping("/{id}")
    public Resort getResort(@PathVariable Integer id) {
        return resortService.getResortById(id);
    }

    @PostMapping
    public Resort createResort(@RequestBody Resort resort) {
        return resortService.saveResort(resort);
    }

    @DeleteMapping("/{id}")
    public void deleteResort(@PathVariable Integer id) {
        resortService.deleteResort(id);
    }
}
