package com.military.mams.config;

import com.military.mams.entity.*;
import com.military.mams.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private EquipmentTypeRepository equipmentTypeRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private PersonnelRepository personnelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private ExpenditureRepository expenditureRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() > 0) {
            return; // Database already seeded
        }

        System.out.println("[MAMS] Starting initial database seeding...");

        // 1. Roles
        Role adminRole = roleRepository.save(new Role(RoleName.ROLE_ADMIN));
        Role commanderRole = roleRepository.save(new Role(RoleName.ROLE_BASE_COMMANDER));
        Role logisticsRole = roleRepository.save(new Role(RoleName.ROLE_LOGISTICS_OFFICER));

        // 2. Bases
        Base base1 = baseRepository.save(new Base("Northern Command Base", "BASE-001", "Sector 4, Udhampur HQ"));
        Base base2 = baseRepository.save(new Base("Western Logistics Depot", "BASE-002", "Depot Area, Chandimandir"));
        Base base3 = baseRepository.save(new Base("Eastern Forward Station", "BASE-003", "Forward Garrison, Tezpur"));

        // 3. Equipment Types
        EquipmentType typeWeapons = equipmentTypeRepository.save(new EquipmentType("Weapons", "EQT-WPN", "Small arms, rifles, missile systems"));
        EquipmentType typeVehicles = equipmentTypeRepository.save(new EquipmentType("Vehicles", "EQT-VEH", "Armored carriers, tactical trucks, utility vehicles"));
        EquipmentType typeAmmo = equipmentTypeRepository.save(new EquipmentType("Ammunition", "EQT-AMM", "Ball ammunition, high-explosive shells, rockets"));

        // 4. Equipment
        Equipment eqRifle = equipmentRepository.save(new Equipment("INSAS 5.56mm Rifle", "EQ-WPN-001", typeWeapons, "Standard infantry assault rifle with tactical optics", "Units"));
        Equipment eqSniper = equipmentRepository.save(new Equipment("7.62mm Sniper System", "EQ-WPN-002", typeWeapons, "Precision long-range sniper rifle system", "Units"));
        Equipment eqMissile = equipmentRepository.save(new Equipment("Anti-Tank Guided Missile", "EQ-WPN-003", typeWeapons, "Man-portable anti-tank missile unit", "Systems"));

        Equipment eqApc = equipmentRepository.save(new Equipment("Armored Personnel Carrier", "EQ-VEH-001", typeVehicles, "Tracked armored troop combat transport vehicle", "Vehicles"));
        Equipment eqTruck = equipmentRepository.save(new Equipment("Heavy Utility 6x6 Truck", "EQ-VEH-002", typeVehicles, "All-terrain high-payload logistics cargo vehicle", "Vehicles"));
        Equipment eqJeep = equipmentRepository.save(new Equipment("Light Recon Patrol Vehicle", "EQ-VEH-003", typeVehicles, "4x4 fast tactical recon patrol vehicle", "Vehicles"));

        Equipment eqAmmo556 = equipmentRepository.save(new Equipment("5.56mm Ammo Crates (2000 rds)", "EQ-AMM-001", typeAmmo, "NATO standard enhanced ball ammunition crates", "Crates"));
        Equipment eqAmmo762 = equipmentRepository.save(new Equipment("7.62mm Precision Ammo Crates", "EQ-AMM-002", typeAmmo, "High-accuracy sniper and medium machine gun ammo", "Crates"));
        Equipment eqRocket = equipmentRepository.save(new Equipment("84mm High-Explosive Rockets", "EQ-AMM-003", typeAmmo, "Infantry shoulder-fired bunker-buster rockets", "Crates"));

        // 5. Users
        String encodedAdminPass = passwordEncoder.encode("admin123");
        String encodedCmdPass = passwordEncoder.encode("commander123");
        String encodedLogPass = passwordEncoder.encode("logistics123");

        User adminUser = userRepository.save(new User("admin@mams.mil", encodedAdminPass, "Arthur Sterling (General)", adminRole, null));
        User cmdUser1 = userRepository.save(new User("commander.north@mams.mil", encodedCmdPass, "Rajiv Sharma (Colonel)", commanderRole, base1));
        User cmdUser2 = userRepository.save(new User("commander.west@mams.mil", encodedCmdPass, "Amit Verma (Colonel)", commanderRole, base2));
        User logUser = userRepository.save(new User("logistics@mams.mil", encodedLogPass, "Sarah Roy (Major)", logisticsRole, base1));

        // 6. Personnel
        Personnel p1 = personnelRepository.save(new Personnel("Major Vikram Batra", "IC-54210", base1, "Company Commander", "Major"));
        Personnel p2 = personnelRepository.save(new Personnel("Captain Manoj Pandey", "IC-61029", base1, "Detachment Commander", "Captain"));
        Personnel p3 = personnelRepository.save(new Personnel("Subedar Balram Singh", "JC-49012", base1, "Armory Custodian JCO", "Subedar Major"));
        Personnel p4 = personnelRepository.save(new Personnel("Major Deepa Joshi", "SS-40291", base2, "Depot Logistics Officer", "Major"));
        Personnel p5 = personnelRepository.save(new Personnel("Captain Rakesh Nair", "IC-55410", base3, "Forward Patrol Leader", "Captain"));

        // 7. Purchases
        Purchase pur1 = purchaseRepository.save(new Purchase("PO-2026-N-001", base1, eqRifle, 150, LocalDate.of(2026, 1, 15), "Ordnance Factory Board", "Routine small arms replenishment", adminUser));
        Purchase pur2 = purchaseRepository.save(new Purchase("PO-2026-N-002", base1, eqAmmo556, 100, LocalDate.of(2026, 2, 10), "Bharat Dynamics Ltd", "Combat unit training stockpile", logUser));
        Purchase pur3 = purchaseRepository.save(new Purchase("PO-2026-N-003", base1, eqApc, 12, LocalDate.of(2026, 3, 5), "Tata Advanced Systems", "Armored transport modernization", adminUser));
        Purchase pur4 = purchaseRepository.save(new Purchase("PO-2026-N-004", base1, eqSniper, 20, LocalDate.of(2026, 4, 12), "Ordnance Factory Board", "High-accuracy rifle allocation", logUser));
        Purchase pur5 = purchaseRepository.save(new Purchase("PO-2026-N-005", base1, eqRocket, 40, LocalDate.of(2026, 5, 20), "Bharat Dynamics Ltd", "Heavy ordnance reserve", logUser));

        Purchase pur6 = purchaseRepository.save(new Purchase("PO-2026-W-001", base2, eqRifle, 100, LocalDate.of(2026, 2, 20), "Ordnance Factory Board", "Depot reserve allocation", adminUser));
        Purchase pur7 = purchaseRepository.save(new Purchase("PO-2026-W-002", base2, eqTruck, 18, LocalDate.of(2026, 3, 18), "Ashok Leyland Defence", "Logistics fleet expansion", adminUser));
        Purchase pur8 = purchaseRepository.save(new Purchase("PO-2026-W-003", base2, eqAmmo556, 80, LocalDate.of(2026, 4, 25), "Bharat Dynamics Ltd", "Depot ammo store", cmdUser2));

        Purchase pur9 = purchaseRepository.save(new Purchase("PO-2026-E-001", base3, eqJeep, 10, LocalDate.of(2026, 3, 22), "Mahindra Defence Systems", "Border patrol vehicle batch", adminUser));
        Purchase pur10 = purchaseRepository.save(new Purchase("PO-2026-E-002", base3, eqMissile, 15, LocalDate.of(2026, 5, 10), "Bharat Dynamics Ltd", "Forward defense missiles", adminUser));

        // 8. Transfers
        Transfer tr1 = transferRepository.save(new Transfer("TO-2026-001", base1, base2, eqRifle, 30, LocalDate.of(2026, 3, 10), "COMPLETED", "Stock redistribution for Western training cycle", adminUser));
        Transfer tr2 = transferRepository.save(new Transfer("TO-2026-002", base2, base3, eqTruck, 4, LocalDate.of(2026, 4, 15), "COMPLETED", "Logistics truck transfer to forward post", adminUser));
        Transfer tr3 = transferRepository.save(new Transfer("TO-2026-003", base1, base3, eqAmmo556, 25, LocalDate.of(2026, 6, 8), "COMPLETED", "Forward ammunition reserve reinforcement", cmdUser1));

        // 9. Assignments
        Assignment asg1 = assignmentRepository.save(new Assignment("ASG-2026-001", p1, base1, eqRifle, 1, LocalDate.of(2026, 3, 1), "Company Commander primary issue", cmdUser1));
        Assignment asg2 = assignmentRepository.save(new Assignment("ASG-2026-002", p2, base1, eqRifle, 1, LocalDate.of(2026, 3, 1), "Detachment field deployment issue", cmdUser1));
        Assignment asg3 = assignmentRepository.save(new Assignment("ASG-2026-003", p3, base1, eqSniper, 2, LocalDate.of(2026, 4, 15), "Sniper marksmanship cadre issue", cmdUser1));
        Assignment asg4 = assignmentRepository.save(new Assignment("ASG-2026-004", p1, base1, eqApc, 1, LocalDate.of(2026, 3, 15), "Section command vehicle allocation", cmdUser1));

        Assignment asg5 = new Assignment("ASG-2026-005", p2, base1, eqRifle, 1, LocalDate.of(2026, 2, 1), "Tactical drill temporary issue", cmdUser1);
        asg5.setStatus("RETURNED");
        asg5.setReturnDate(LocalDate.of(2026, 2, 28));
        asg5.setRemarks("Inspected and cleaned. Returned to armory.");
        assignmentRepository.save(asg5);

        // 10. Expenditures
        Expenditure exp1 = expenditureRepository.save(new Expenditure("EXP-2026-001", base1, eqAmmo556, 20, LocalDate.of(2026, 3, 20), "Live Fire Training Exercise", "Annual infantry qualification firing table V", cmdUser1));
        Expenditure exp2 = expenditureRepository.save(new Expenditure("EXP-2026-002", base1, eqRocket, 6, LocalDate.of(2026, 5, 25), "Combat Simulation Drill", "Anti-armor firing exercise Range 12", cmdUser1));
        Expenditure exp3 = expenditureRepository.save(new Expenditure("EXP-2026-003", base2, eqAmmo556, 15, LocalDate.of(2026, 5, 12), "Routine Marksmanship Testing", "Sidearm and rifle qualification range", cmdUser2));

        // 11. Audit Logs
        auditLogRepository.save(new AuditLog("admin@mams.mil", "ROLE_ADMIN", "SYSTEM_BOOTSTRAP", "System", 1L, null, "Initialized MAMS enterprise defense ledger schema and base catalog", "127.0.0.1"));
        auditLogRepository.save(new AuditLog("admin@mams.mil", "ROLE_ADMIN", "PURCHASE_CREATED", "Purchase", pur1.getId(), null, "Procured 150x INSAS Rifles for Northern Command Base", "127.0.0.1"));
        auditLogRepository.save(new AuditLog("commander.north@mams.mil", "ROLE_BASE_COMMANDER", "TRANSFER_EXECUTED", "Transfer", tr1.getId(), null, "Transferred 30x INSAS Rifles from BASE-001 to BASE-002", "127.0.0.1"));
        auditLogRepository.save(new AuditLog("commander.north@mams.mil", "ROLE_BASE_COMMANDER", "ASSIGNMENT_CREATED", "Assignment", asg1.getId(), null, "Assigned INSAS Rifle to Major Vikram Batra", "127.0.0.1"));
        auditLogRepository.save(new AuditLog("commander.north@mams.mil", "ROLE_BASE_COMMANDER", "EXPENDITURE_RECORDED", "Expenditure", exp1.getId(), null, "Expended 20x 5.56mm Ammo Crates during Live Fire Exercise", "127.0.0.1"));

        System.out.println("[MAMS] Database seeded successfully!");
        System.out.println("[MAMS] Admin: admin@mams.mil / admin123");
        System.out.println("[MAMS] Base Commander (North): commander.north@mams.mil / commander123");
        System.out.println("[MAMS] Base Commander (West): commander.west@mams.mil / commander123");
        System.out.println("[MAMS] Logistics Officer: logistics@mams.mil / logistics123");
    }
}
