import android.os.Parcel
import android.os.Parcelable

data class JobPosting(
    val job_listnum: Int,
    val company_name: String,
    val job_title: String,
    val job_experience_required: String,
    val job_education_required: String,
    val job_period: String,
    val job_days_of_week: String,
    val job_working_hours: String,
    val job_salary: String,
    val job_position: String,
    val job_category: String,
    val job_requirements: String,
    val job_contact_number: String,
    val job_email: String,
    val job_deadline: String
) : Parcelable {
    // Parcelable 인터페이스 구현
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        // 필드를 parcel에 쓰는 코드 추가
        parcel.writeInt(job_listnum)
        parcel.writeString(company_name)  // 변경된 필드명으로 수정
        parcel.writeString(job_title)
        parcel.writeString(job_experience_required)
        parcel.writeString(job_education_required)
        parcel.writeString(job_period)
        parcel.writeString(job_days_of_week)
        parcel.writeString(job_working_hours)
        parcel.writeString(job_salary)
        parcel.writeString(job_position)
        parcel.writeString(job_category)
        parcel.writeString(job_requirements)
        parcel.writeString(job_contact_number)
        parcel.writeString(job_email)
        parcel.writeString(job_deadline)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<JobPosting> {
        override fun createFromParcel(parcel: Parcel): JobPosting {
            // Parcel에서 필드를 읽어와서 JobPosting 객체를 생성하는 코드 추가
            return JobPosting(
                parcel.readInt(),
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: ""
            )
        }

        override fun newArray(size: Int): Array<JobPosting?> {
            return arrayOfNulls(size)
        }
    }
}
