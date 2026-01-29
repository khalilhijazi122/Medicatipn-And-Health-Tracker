package com.example.medicatiooandhealthtrackerthemain;

public class AdherenceData {

    private int takenCount;     // Number of medications taken
    private int missedCount;    // Number of medications missed
    private int skippedCount;   // Number of medications deliberately skipped
    private int pendingCount;   // Number of upcoming medications

    // ==================== CONSTRUCTORS ====================

    /**
     * Basic constructor with taken and missed counts
     */
    public AdherenceData(int takenCount, int missedCount) {
        this.takenCount = takenCount;
        this.missedCount = missedCount;
        this.skippedCount = 0;
        this.pendingCount = 0;
    }

    /**
     * Full constructor with all counts
     */
    public AdherenceData(int takenCount, int missedCount, int skippedCount, int pendingCount) {
        this.takenCount = takenCount;
        this.missedCount = missedCount;
        this.skippedCount = skippedCount;
        this.pendingCount = pendingCount;
    }

    // ==================== GETTERS ====================

    public int getTakenCount() {
        return takenCount;
    }

    public int getMissedCount() {
        return missedCount;
    }

    public int getSkippedCount() {
        return skippedCount;
    }

    public int getPendingCount() {
        return pendingCount;
    }

    // ==================== SETTERS ====================

    public void setTakenCount(int takenCount) {
        this.takenCount = takenCount;
    }

    public void setMissedCount(int missedCount) {
        this.missedCount = missedCount;
    }

    public void setSkippedCount(int skippedCount) {
        this.skippedCount = skippedCount;
    }

    public void setPendingCount(int pendingCount) {
        this.pendingCount = pendingCount;
    }

    // ==================== CALCULATED PROPERTIES ====================

    /**
     * Get total number of scheduled doses (excluding pending)
     * @return Total doses that should have been taken
     */
    public int getTotalScheduledCount() {
        return takenCount + missedCount + skippedCount;
    }

    /**
     * Get total including pending medications
     * @return Total doses including future ones
     */
    public int getTotalWithPendingCount() {
        return takenCount + missedCount + skippedCount + pendingCount;
    }

    /**
     * Calculate adherence percentage (excludes pending and skipped)
     * Formula: (taken / (taken + missed)) * 100
     * @return Adherence percentage (0-100)
     */
    public int getAdherencePercentage() {
        int total = takenCount + missedCount;
        if (total == 0) {
            return 0; // Avoid division by zero
        }
        return (takenCount * 100) / total;
    }

    /**
     * Calculate adherence percentage including skipped doses
     * Formula: (taken / (taken + missed + skipped)) * 100
     * @return Adherence percentage (0-100)
     */
    public int getAdherencePercentageWithSkipped() {
        int total = getTotalScheduledCount();
        if (total == 0) {
            return 0;
        }
        return (takenCount * 100) / total;
    }

    /**
     * Get adherence level as text
     * @return "Excellent", "Good", "Fair", or "Poor"
     */
    public String getAdherenceLevel() {
        int percentage = getAdherencePercentage();

        if (percentage >= 90) {
            return "Excellent";
        } else if (percentage >= 75) {
            return "Good";
        } else if (percentage >= 60) {
            return "Fair";
        } else {
            return "Needs Improvement";
        }
    }

    /**
     * Get color resource for adherence level
     * @return Color name as string
     */
    public String getAdherenceColor() {
        int percentage = getAdherencePercentage();

        if (percentage >= 90) {
            return "success";  // Green
        } else if (percentage >= 70) {
            return "warning";  // Orange
        } else {
            return "error";    // Red
        }
    }

    /**
     * Check if adherence is good (>= 80%)
     * @return true if adherence is 80% or higher
     */
    public boolean isGoodAdherence() {
        return getAdherencePercentage() >= 80;
    }

    // ==================== UTILITY METHODS ====================

    @Override
    public String toString() {
        return "AdherenceData{" +
                "takenCount=" + takenCount +
                ", missedCount=" + missedCount +
                ", skippedCount=" + skippedCount +
                ", pendingCount=" + pendingCount +
                ", adherence=" + getAdherencePercentage() + "%" +
                ", level=" + getAdherenceLevel() +
                '}';
    }
}