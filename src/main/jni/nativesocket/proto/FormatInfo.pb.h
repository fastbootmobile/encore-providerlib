// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: FormatInfo.proto

#ifndef PROTOBUF_FormatInfo_2eproto__INCLUDED
#define PROTOBUF_FormatInfo_2eproto__INCLUDED

#include <string>

#include <google/protobuf/stubs/common.h>

#if GOOGLE_PROTOBUF_VERSION < 2005000
#error This file was generated by a newer version of protoc which is
#error incompatible with your Protocol Buffer headers.  Please update
#error your headers.
#endif
#if 2005000 < GOOGLE_PROTOBUF_MIN_PROTOC_VERSION
#error This file was generated by an older version of protoc which is
#error incompatible with your Protocol Buffer headers.  Please
#error regenerate this file with a newer version of protoc.
#endif

#include <google/protobuf/generated_message_util.h>
#include <google/protobuf/message.h>
#include <google/protobuf/repeated_field.h>
#include <google/protobuf/extension_set.h>
#include <google/protobuf/unknown_field_set.h>
// @@protoc_insertion_point(includes)

namespace omnimusic {

// Internal implementation detail -- do not call these.
void  protobuf_AddDesc_FormatInfo_2eproto();
void protobuf_AssignDesc_FormatInfo_2eproto();
void protobuf_ShutdownFile_FormatInfo_2eproto();

class FormatInfo;

// ===================================================================

class FormatInfo : public ::google::protobuf::Message {
 public:
  FormatInfo();
  virtual ~FormatInfo();

  FormatInfo(const FormatInfo& from);

  inline FormatInfo& operator=(const FormatInfo& from) {
    CopyFrom(from);
    return *this;
  }

  inline const ::google::protobuf::UnknownFieldSet& unknown_fields() const {
    return _unknown_fields_;
  }

  inline ::google::protobuf::UnknownFieldSet* mutable_unknown_fields() {
    return &_unknown_fields_;
  }

  static const ::google::protobuf::Descriptor* descriptor();
  static const FormatInfo& default_instance();

  void Swap(FormatInfo* other);

  // implements Message ----------------------------------------------

  FormatInfo* New() const;
  void CopyFrom(const ::google::protobuf::Message& from);
  void MergeFrom(const ::google::protobuf::Message& from);
  void CopyFrom(const FormatInfo& from);
  void MergeFrom(const FormatInfo& from);
  void Clear();
  bool IsInitialized() const;

  int ByteSize() const;
  bool MergePartialFromCodedStream(
      ::google::protobuf::io::CodedInputStream* input);
  void SerializeWithCachedSizes(
      ::google::protobuf::io::CodedOutputStream* output) const;
  ::google::protobuf::uint8* SerializeWithCachedSizesToArray(::google::protobuf::uint8* output) const;
  int GetCachedSize() const { return _cached_size_; }
  private:
  void SharedCtor();
  void SharedDtor();
  void SetCachedSize(int size) const;
  public:

  ::google::protobuf::Metadata GetMetadata() const;

  // nested types ----------------------------------------------------

  // accessors -------------------------------------------------------

  // required int32 sampling_rate = 1;
  inline bool has_sampling_rate() const;
  inline void clear_sampling_rate();
  static const int kSamplingRateFieldNumber = 1;
  inline ::google::protobuf::int32 sampling_rate() const;
  inline void set_sampling_rate(::google::protobuf::int32 value);

  // required int32 channels = 2;
  inline bool has_channels() const;
  inline void clear_channels();
  static const int kChannelsFieldNumber = 2;
  inline ::google::protobuf::int32 channels() const;
  inline void set_channels(::google::protobuf::int32 value);

  // @@protoc_insertion_point(class_scope:omnimusic.FormatInfo)
 private:
  inline void set_has_sampling_rate();
  inline void clear_has_sampling_rate();
  inline void set_has_channels();
  inline void clear_has_channels();

  ::google::protobuf::UnknownFieldSet _unknown_fields_;

  ::google::protobuf::int32 sampling_rate_;
  ::google::protobuf::int32 channels_;

  mutable int _cached_size_;
  ::google::protobuf::uint32 _has_bits_[(2 + 31) / 32];

  friend void  protobuf_AddDesc_FormatInfo_2eproto();
  friend void protobuf_AssignDesc_FormatInfo_2eproto();
  friend void protobuf_ShutdownFile_FormatInfo_2eproto();

  void InitAsDefaultInstance();
  static FormatInfo* default_instance_;
};
// ===================================================================


// ===================================================================

// FormatInfo

// required int32 sampling_rate = 1;
inline bool FormatInfo::has_sampling_rate() const {
  return (_has_bits_[0] & 0x00000001u) != 0;
}
inline void FormatInfo::set_has_sampling_rate() {
  _has_bits_[0] |= 0x00000001u;
}
inline void FormatInfo::clear_has_sampling_rate() {
  _has_bits_[0] &= ~0x00000001u;
}
inline void FormatInfo::clear_sampling_rate() {
  sampling_rate_ = 0;
  clear_has_sampling_rate();
}
inline ::google::protobuf::int32 FormatInfo::sampling_rate() const {
  return sampling_rate_;
}
inline void FormatInfo::set_sampling_rate(::google::protobuf::int32 value) {
  set_has_sampling_rate();
  sampling_rate_ = value;
}

// required int32 channels = 2;
inline bool FormatInfo::has_channels() const {
  return (_has_bits_[0] & 0x00000002u) != 0;
}
inline void FormatInfo::set_has_channels() {
  _has_bits_[0] |= 0x00000002u;
}
inline void FormatInfo::clear_has_channels() {
  _has_bits_[0] &= ~0x00000002u;
}
inline void FormatInfo::clear_channels() {
  channels_ = 0;
  clear_has_channels();
}
inline ::google::protobuf::int32 FormatInfo::channels() const {
  return channels_;
}
inline void FormatInfo::set_channels(::google::protobuf::int32 value) {
  set_has_channels();
  channels_ = value;
}


// @@protoc_insertion_point(namespace_scope)

}  // namespace omnimusic

#ifndef SWIG
namespace google {
namespace protobuf {


}  // namespace google
}  // namespace protobuf
#endif  // SWIG

// @@protoc_insertion_point(global_scope)

#endif  // PROTOBUF_FormatInfo_2eproto__INCLUDED
