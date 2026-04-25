package com.shredshare.ShredShare.Service;

import com.shredshare.ShredShare.Entity.Resort;
import com.shredshare.ShredShare.Repository.ResortRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResortService {

    private final ResortRepository resortRepository;

    public ResortService(ResortRepository resortRepository) {
        this.resortRepository = resortRepository;
    }

    public List<Resort> getAllResorts() {
        return resortRepository.findAll();
    }

    public Resort getResortById(Integer id) {
        return resortRepository.findById(id).orElse(null);
    }

    public Resort saveResort(Resort resort) {
        return resortRepository.save(resort);
    }

    public void deleteResort(Integer id) {
        resortRepository.deleteById(id);
    }
}
