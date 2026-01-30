package com.thantruongnhan.doanketthucmon.controller;

import com.thantruongnhan.doanketthucmon.entity.TableEntity;
import com.thantruongnhan.doanketthucmon.repository.TableRepository;
import com.thantruongnhan.doanketthucmon.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/tables")
@CrossOrigin(origins = "http://localhost:3000")
public class TableController {

    @Autowired
    private TableService tableService;
    @Autowired
    private TableRepository tableRepository;

    // Xem danh sách bàn
    @GetMapping
    public List<TableEntity> getAllTables() {
        return tableService.getAllTables();
    }

    // Xem chi tiết bàn
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER' , 'EMPLOYEE', 'CUSTOMER', 'KITCHEN')")
    public TableEntity getTableById(@PathVariable Long id) {
        return tableService.getTableById(id);
    }

    // Nhân viên chọn bàn (đổi trạng thái sang OCCUPIED)
    @PutMapping("/{id}/occupy")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE', 'CUSTOMER')")
    public TableEntity occupyTable(@PathVariable Long id) {
        return tableService.occupyTable(id);
    }

    // Nhân viên trả bàn (đổi trạng thái sang FREE)
    @PutMapping("/{id}/free")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE')")
    public TableEntity freeTable(@PathVariable Long id) {
        return tableService.freeTable(id);
    }

    // Thêm bàn (Chỉ Admin)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public TableEntity createTable(@RequestBody TableEntity table) {
        return tableService.createTable(table);
    }

    // Cập nhật bàn (Chỉ Admin)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public TableEntity updateTable(@PathVariable Long id, @RequestBody TableEntity table) {
        return tableService.updateTable(id, table);
    }

    // Xóa bàn (Chỉ Admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
    }

    // Lấy danh sách area theo branch
    @GetMapping("/branch/{branchId}/areas")
    public List<String> getAreasByBranch(@PathVariable Long branchId) {
        return tableRepository.findDistinctAreasByBranchId(branchId);
    }

    // Lấy bàn theo branch và area
    @GetMapping("/branch/{branchId}/area/{area}")
    public List<TableEntity> getTablesByBranchAndArea(
            @PathVariable Long branchId,
            @PathVariable String area) {
        return tableRepository.findByBranchIdAndArea(branchId, area);
    }
}
